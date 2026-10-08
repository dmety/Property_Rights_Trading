package cn.edu.cjxy.iotlink.mapper;

import cn.edu.cjxy.iotlink.common.Result;
import cn.edu.cjxy.iotlink.model.SysDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blockexplore.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DeviceDao extends BaseMapper<SysDevice> {
    List<SysDevice> getDeviceList();
    void updateDeviceStatus(@Param("status") String status,@Param("id")  Long id);
    void deleteDevice(Long id);
    void deleteDeviceData(Long deviceId);
    void deleteProjectDevice(Long deviceId);
    List<SysDevice> getDeviceListByTaskId(Long taskId);
    List<SysDevice> getAllSensorDevices();
    void saveDeviceAlarmStatus(Long id, String status);
    List<User> getDeviceUserList();
    List<SysDevice> getDeviceByNodeIdAndDevAddress(String nodeId, String devAddress);

    int selectByName(String user);

    String queryCode(Long id);

    void updateCode(String projectCode);

    void updateCP(@Param("id") Long id, @Param("point") String point, @Param("code") String code);

    List<SysDevice> selectDevByName(String name);
}
