package cn.edu.cjxy.iotlink.service.impl;

import cn.edu.cjxy.iotlink.common.Result;
import cn.edu.cjxy.iotlink.config.MqttConfig;
import cn.edu.cjxy.iotlink.mapper.DeviceDao;
import cn.edu.cjxy.iotlink.mapper.DeviceDataDao;
import cn.edu.cjxy.iotlink.model.SysDevData;
import cn.edu.cjxy.iotlink.model.SysDevice;
import cn.edu.cjxy.iotlink.mqtt.MqttFactory;
import cn.edu.cjxy.iotlink.service.DeviceService;
import cn.edu.cjxy.iotlink.util.HexUtils;
import cn.edu.cjxy.iotlink.util.TimeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.ProjectDao;
import com.blockexplore.utils.HttpUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.blockexplore.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService, ApplicationContextAware {
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    DeviceDataDao deviceDataDao;
    @Autowired
    ProjectDao projectDao;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Result<List<SysDevice>> getDeviceList() {
        return Result.success(deviceDao.getDeviceList());
    }

    @Override
    public Result<SysDevice> getDeviceById(Long id) {
        return Result.success(deviceDao.selectById(id));
    }

    @Override
    public SysDevice getDevice(Long id) {
        return deviceDao.selectById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public Result<String> saveDevice(SysDevice device) {
        String user = device.getUser();
        // 插入数据库
        int id = deviceDao.selectByName(user);
        String code = device.getProjectCode();
        device.setUser(user);
        device.setUserId(id);
        device.setProjectCode(code);
        device.setProjectCode(device.getProjectCode());
        if(!device.getName().isEmpty()) {
            if("传感器设备".equals(device.getType())) {
                device.setDataStart(null);
                device.setDataStop(null);
                device.setStartCmd(null);
                device.setStopCmd(null);
            }
            else {
                device.setDataLen(null);
                device.setCollectCmd(null);
//                device.setTaskId(0L);
                device.setThreshold(null);
                device.setDataName(null);
                device.setDataRange(null);
                device.setAlarm(null);
            }
            if(device.getStatus() == null) {
                device.setStatus("0");
            }
            if (device.getId() != null) {
                deviceDao.updateById(device);
            }
            else {
                device.setCreateTime(TimeUtil.getCurrentDateTime());
                deviceDao.insert(device);
            }
            return Result.success("设备数据保存成功！");
        }
        else {
            return Result.failure("设备名称不能为空！");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public int saveDeviceData(SysDevData deviceData) {
        return deviceDataDao.insert(deviceData);
    }

    @Override
    public Result<List<SysDevData>> getDeviceDataList(Long deviceId) {
        return Result.success(deviceDataDao.getDeviceDataList(deviceId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public Result<String> deleteDevice(Long id) {
        deviceDao.deleteDevice(id);
        deviceDao.deleteDeviceData(id);
        return Result.success("设备删除成功！");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public Result<String> updateDeviceStatus(String status, Long id) {
        deviceDao.updateDeviceStatus(status, id);
        return Result.success("设备状态修改成功！");
    }

    @Override
    public List<SysDevice> getDeviceListByTaskId(Long taskId) {
        return deviceDao.getDeviceListByTaskId(taskId);
    }

    @Override
    public List<SysDevice> getAllSensorDevices() {
        return deviceDao.getAllSensorDevices();
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public Result<String> startDevice(Long id, String pointName, String projectCode) {
        int count = projectDao.selectByCode(projectCode);
        if(count == 0){
            return Result.failure("该项目编号错误");
        }else {
            //更新设备所属的项目编号和采集点名称
            deviceDao.updateCP(id,pointName,projectCode);
            deviceDataDao.updateIPC(id,pointName,projectCode);

            SysDevice device = null;
            try {
                // 创建mqtt服务
                MqttConfig mqttConfig = applicationContext.getBean(MqttConfig.class);
                MqttFactory mqttFactory = applicationContext.getBean(MqttFactory.class);
                // 通过设备id获取设备信息
                device = deviceDao.selectById(id);
                String nodeId = device.getNodeId();
                MqttClient client = mqttFactory.getInstance();
                String deviceSubTopic = mqttConfig.getDeviceSubTopic();

                // 发送采集命令（collectCmd:采集指令）
                String collectCmd = device.getCollectCmd();
                if (collectCmd != null && !collectCmd.isEmpty()) {
                    String[] cmdArray = collectCmd.split("\\|");
                    for (String cmd : cmdArray) {
                        byte[] collectData = HexUtils.hexStringToByteArray(nodeId + cmd);
                        MqttMessage collectMessage = new MqttMessage(collectData);
                        client.publish(deviceSubTopic, collectMessage);
                    }
                    return Result.custom(200, "启动设备并采集命令已发送成功！", null);
                }else return Result.failure("设备启动失败，原因：采集指令为空");

            } catch (Exception e) {
                e.printStackTrace();
                return Result.failure("设备启动失败，原因：" + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public Result<String> stopDevice(Long id) {
        try {
            MqttConfig mqttConfig = applicationContext.getBean(MqttConfig.class);
            MqttFactory mqttFactory = applicationContext.getBean(MqttFactory.class);
            SysDevice device = deviceDao.selectById(id);
            String stopCmd = device.getStopCmd();
            String nodeId = device.getNodeId();
            byte[] hexData = HexUtils.hexStringToByteArray(nodeId + stopCmd);
            MqttMessage message = new MqttMessage(hexData);
            String deviceSubTopic = mqttConfig.getDeviceSubTopic();
            MqttClient client = mqttFactory.getInstance();
            client.publish(deviceSubTopic, message);
            deviceDao.updateDeviceStatus("0", id);//更新设备离线

            return Result.custom(200,"停止设备命令已成功发送!", null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Result.failure("设备停止失败，原因：：" + e.getMessage());
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
    public void saveDeviceAlarmStatus(Long id, String status) {
        deviceDao.saveDeviceAlarmStatus(id, status);
    }

    @Override
    public String[] getDeviceUserMails(Long id) {
        List<User> userList = deviceDao.getDeviceUserList();
        if(userList.size()>0) {
            String[] userEmails = new String[userList.size()];
            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                userEmails[i] = user.getEmail();
            }
            return userEmails;
        }
        return null;
    }

    @Override
    public SysDevice getDeviceByNodeIdAndDevAddress(String nodeId, String devAddress) {
        List<SysDevice> devices = deviceDao.getDeviceByNodeIdAndDevAddress(nodeId, devAddress);
        if (devices.size()>0) {
            return devices.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public boolean updateByICP(Long id, String code, String point, String time, String dataName, String dataValue) {
        List<Object> param = new ArrayList<>();
        param.add(code);
        param.add(point);
        param.add(dataName);
        param.add(dataValue);
        param.add(id);
        param.add(time);

        HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"updatePoint",param);
        return deviceDataDao.updateByICP( id,  code,  point,  time,  dataName,  dataValue);
    }

    @Override
    public Result<List<SysDevice>> selectDevByName(String name) {
        List<SysDevice> sysDevices = deviceDao.selectDevByName(name);
        return Result.success(sysDevices);
    }

    @Override
    public Result<String> addPoint(Long id,String point, String code) {
        int count = projectDao.selectByCode(code);
        if(count == 0){
            return Result.failure("该项目编号错误");
        }else {
            int res = deviceDataDao.addPoint(id,point,code);
            if (res > 0){
                return Result.custom(200,"采集点添加成功",null);
            }else {
                return Result.failure("添加失败");
            }
        }
    }

    @Override
    public Result<String> delPoint(String point, String code) {
        List<Object> params = new ArrayList<>();
        params.add(code);
        params.add(point);

        String response = HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS, "delPoint", params);
        JSONObject json = JSONUtil.parseObj(response);
        Integer status = json.getInt("code");
        if (status != null && status == 0) {
            int result = deviceDataDao.deletePoint(point, code);
            if (result > 0) {
                return Result.custom(200, "采集点删除成功", null);
            } else {
                return Result.custom(404, "采集点不存在或删除失败", null);
            }
        }else {
            return Result.failure("调用合约失败");
        }


    }

    @Override
    public Result<List<SysDevData>> getDeviceData(Long id, String code) {
         List<SysDevData> sysDevDatas = deviceDataDao.selectByIC(id,code);
         if (sysDevDatas==null){
             return Result.failure("该数据不存在");
         }else
             return Result.success(sysDevDatas);
    }

    @Override
    public Result<String> addPointByPhone(String point, String code) {
        int count = projectDao.selectByCode(code);
        if(count == 0){
            return Result.failure("该项目编号错误");
        }else {
            List<Object> param = new ArrayList<>();
            param.add(point);
            param.add(code);
            JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addPoint",param));
            if (result.getBool("statusOK")){
                int res = deviceDataDao.addPoint(3L,point,code);
                if (res > 0){
                    return Result.custom(200,"采集点添加成功",null);
                }else {
                    return Result.failure("添加失败");
                }
            }else {
                return Result.failure("调用合约失败");
            }

        }
    }

    @Override
    public Result<List<SysDevData>> getDeviceDataByPhone(String code) {
        List<String> params = new ArrayList<>();
        params.add(code);
        String response = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS, "getAllPoints", params); // 修改为你实际使用的 userAddress
//        JSONObject json = JSONUtil.parseObj(response);
        JSONArray jsonArray = JSONUtil.parseArray(response);
        // 检查是否获取到预期数量的数组（根据原始代码应为6个）
        if (jsonArray == null || jsonArray.size() != 6) {
            return Result.failure("合约返回数据格式错误");
        }

        // 获取每个子数组
        JSONArray projectCodes = jsonArray.getJSONArray(0);
        JSONArray pointNames = jsonArray.getJSONArray(1);
        JSONArray dataNames = jsonArray.getJSONArray(2);
        JSONArray dataValues = jsonArray.getJSONArray(3);
        JSONArray deviceIds = jsonArray.getJSONArray(4);
        JSONArray collectTimes = jsonArray.getJSONArray(5);
        List<SysDevData> dataList = new ArrayList<>();
        // 由于示例中所有数组均为空，该循环不会执行
        for (int i = 0; i < pointNames.size(); i++) {
            SysDevData data = new SysDevData();
            data.setProjectCode(projectCodes.getStr(i));
            data.setAcquisitionPoint(pointNames.getStr(i));
            data.setDataName(dataNames.getStr(i));
            data.setDataValue(dataValues.getStr(i));
            data.setDeviceId(Long.parseLong(deviceIds.getStr(i)));
            data.setCollectTime(collectTimes.getStr(i));
            dataList.add(data);
        }
        if (dataList.isEmpty()) {
            return Result.failure("该项目下无采集点数据");
        } else {
            return Result.success(dataList);
        }
    }
}
