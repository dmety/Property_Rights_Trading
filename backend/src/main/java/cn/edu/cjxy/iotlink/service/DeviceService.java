package cn.edu.cjxy.iotlink.service;

import cn.edu.cjxy.iotlink.common.Result;
import cn.edu.cjxy.iotlink.model.SysDevData;
import cn.edu.cjxy.iotlink.model.SysDevice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DeviceService {
    Result<List<SysDevice>> getDeviceList();

    Result<SysDevice> getDeviceById(Long id);

    SysDevice getDevice(Long id);

    Result<String> saveDevice(SysDevice device);

    int saveDeviceData(SysDevData deviceData);

    Result<List<SysDevData>> getDeviceDataList(Long deviceId);

    Result<String> deleteDevice(Long id);

    Result<String> updateDeviceStatus(String status, Long id);

    List<SysDevice> getDeviceListByTaskId(Long taskId);

    List<SysDevice> getAllSensorDevices();

    Result<String> startDevice(Long id,String point, String code);

    Result<String> stopDevice(Long id);

    void saveDeviceAlarmStatus(Long id, String status);

    String[] getDeviceUserMails(Long id);

    SysDevice getDeviceByNodeIdAndDevAddress(String nodeId, String devAddress);

    boolean updateByICP(Long id, String code, String point, String time, String dataName, String dataValue);

    Result<List<SysDevice>> selectDevByName(String name);

    Result<String> addPoint(Long id,String point, String code);

    Result<String> delPoint(String point, String code);

    Result<List<SysDevData>> getDeviceData(Long id, String code);

    Result<String> addPointByPhone(String point, String code);

    Result<List<SysDevData>> getDeviceDataByPhone(String code);
}
