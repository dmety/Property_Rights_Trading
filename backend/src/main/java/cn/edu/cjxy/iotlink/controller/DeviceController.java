package cn.edu.cjxy.iotlink.controller;

import cn.edu.cjxy.iotlink.common.Result;
import cn.edu.cjxy.iotlink.model.SysDevData;
import cn.edu.cjxy.iotlink.model.SysDevice;
import cn.edu.cjxy.iotlink.service.DeviceService;
import cn.edu.cjxy.iotlink.util.HexUtils;
import cn.edu.cjxy.iotlink.util.ModbusCRC;
import cn.edu.cjxy.iotlink.util.ThreadLocalUtil;
import com.baomidou.mybatisplus.extension.api.R;
import org.apache.ibatis.annotations.Param;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;

/**
 * 设备控制器
 * 处理物联网设备相关的所有REST API请求
 * 所有方法成功返回码：200，失败返回码：500
 */
@RestController
@CrossOrigin
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    DeviceService deviceService;

    /**
     * 获取设备列表
     * @return 操作结果，包含设备列表数据，成功码200，失败码500
     */
    @GetMapping("/getDeviceList")
    public Result<List<SysDevice>> getDeviceList() {
        return deviceService.getDeviceList();
    }

    /**
     * 根据ID获取设备信息
     * @param id 设备ID
     * @return 操作结果，包含设备详细信息，成功码200，失败码500
     */
    @GetMapping("/getDevice")
    public Result<SysDevice> getDevice(@RequestParam("id") Long id) {
        return deviceService.getDeviceById(id);
    }

    /**
     * 保存设备信息
     * @param device 设备对象
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/saveDevice")
    public Result<String> saveDevice(@RequestBody SysDevice device) {
        return deviceService.saveDevice(device);
    }

    /**
     * 删除设备
     * @param id 设备ID
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/deleteDevice")
    public Result<String> deleteDevice(@RequestParam("id") Long id) {
        return deviceService.deleteDevice(id);
    }

    /**
     * 更新设备状态
     * @param status 设备状态
     * @param id 设备ID
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/updateDeviceStatus")
    public Result<String> updateDeviceStatus(@RequestParam("status") String status, @RequestParam("id") Long id) {
        return deviceService.updateDeviceStatus(status, id);
    }

    /**
     * 获取设备数据列表
     * @param id 设备ID
     * @return 操作结果，包含设备数据列表，成功码200，失败码500
     */
    @GetMapping("/getDeviceDataList")
    public Result<List<SysDevData>> getDeviceDataList(@RequestParam("id") Long id) {
        return deviceService.getDeviceDataList(id);
    }

    /**
     * 获取指定设备数据
     * @param id 设备ID
     * @param code 数据编码
     * @return 操作结果，包含指定设备数据，成功码200，失败码500
     */
    @GetMapping("/getDeviceData")
    public Result<List<SysDevData>> getDeviceData(@RequestParam("id") Long id, @RequestParam("code") String code) {
        return deviceService.getDeviceData(id, code);
    }

    /**
     * 停止设备运行
     * @param id 设备ID
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/stopDevice")
    public Result<String> stopDevice(@RequestParam("id") Long id) {
        return deviceService.stopDevice(id);
    }

    /**
     * 计算CRC校验码
     * @param hexData 十六进制数据字符串
     * @return 操作结果，包含CRC校验码，成功码200，失败码500
     */
    @PostMapping("/getCRC")
    public Result<String> getCRC(@RequestParam("data") String hexData) {
        String crc = ModbusCRC.calculateCRC(HexUtils.hexStringToByteArray(hexData));
        return Result.custom(200, crc, null);
    }

    /**
     * 启动传感器数据采集
     * @param id 设备ID
     * @param pointName 采集点名称
     * @param projectCode 项目编号
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/start")
    public Result<String> start(@RequestParam("id") Long id,
                                @RequestParam("pointName") String pointName,
                                @RequestParam("projectCode") String projectCode) {
        return deviceService.startDevice(id, pointName, projectCode);
    }

    /**
     * 添加采集点
     * @param id 设备ID
     * @param point 采集点
     * @param code 项目编号
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/xxaddPointxx")
    public Result<String> addPoint(@RequestParam("id") Long id, @RequestParam("point") String point, @RequestParam("code") String code) {
        return deviceService.addPoint(id, point, code);
    }

    /**
     * 删除采集点
     * @param point 采集点
     * @param code 项目编号
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("delPoint")
    public Result<String> delPoint(@RequestParam String point, @RequestParam String code) {
        return deviceService.delPoint(point, code);
    }

    // 手机端接口

    /**
     * (手机端)新增采集点
     * @param point 采集点名称
     * @param code 项目编号
     * @return 操作结果，成功码200，失败码500
     */
    @PostMapping("/addPointByPhone")
    public Result<String> addPointByPhone(@RequestParam("pointName") String point,
                                          @RequestParam("projectCode") String code) {
        return deviceService.addPointByPhone(point, code);
    }

    /**
     * 根据项目编号查询所有采集点数据
     * @param code 项目编号
     * @return 操作结果，包含采集点数据列表，成功码200，失败码500
     */
    @PostMapping("/getDeviceDataByPhone")
    public Result<List<SysDevData>> getDeviceDataByPhone(@RequestParam("projectCode") String code) {
        return deviceService.getDeviceDataByPhone(code);
    }

    /**
     * 根据用户名查询该用户的设备
     * @param name 用户名
     * @return 操作结果，包含设备列表，成功码200，失败码500
     */
    @PostMapping("/queryDev")
    public Result<List<SysDevice>> selectDevByName(@RequestParam("name") String name) {
        return deviceService.selectDevByName(name);
    }
}
