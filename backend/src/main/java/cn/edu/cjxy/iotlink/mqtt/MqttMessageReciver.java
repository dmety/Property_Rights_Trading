package cn.edu.cjxy.iotlink.mqtt;

import cn.edu.cjxy.iotlink.model.SysDevData;
import cn.edu.cjxy.iotlink.model.SysDevice;
import cn.edu.cjxy.iotlink.service.DeviceService;
import cn.edu.cjxy.iotlink.util.HexUtils;
import cn.edu.cjxy.iotlink.util.MailUtil;
import cn.edu.cjxy.iotlink.util.ThreadLocalUtil;
import cn.edu.cjxy.iotlink.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Slf4j
public class MqttMessageReciver implements IMqttMessageListener {

    private String devicePubTopic;
    private DeviceService deviceService;

    public MqttMessageReciver(String devicePubTopic, DeviceService deviceService) {
        this.devicePubTopic = devicePubTopic;
        this.deviceService = deviceService;
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        log.info("Message Arrived On Topic: " + topic);
        String hexData = HexUtils.bytesToHex(mqttMessage.getPayload());
        System.out.println("---------------------------------");
        log.info("Received Message HEX: " + hexData);
        if(devicePubTopic.equals(topic)) {
            List<SysDevice> deviceList = deviceService.getAllSensorDevices();
            for (SysDevice device : deviceList) {
                try {
                    log.info("+++++++++++++++++++++++++++++++++++++++++++++" + device.getProjectCode());
                    String hexStr = HexUtils.bytesToHex(mqttMessage.getPayload());
                    String prefix = device.getNodeId() + device.getDevAddress();
                    if(hexStr.toLowerCase().startsWith(prefix.toLowerCase())) {
                        String[] dataNames = device.getDataName().split("\\|");
                        String[] thresholds = device.getThreshold().split("\\|");
                        String[] dataRanges = device.getDataRange().split("\\|");
                        if(dataNames.length>0 && dataNames.length==dataRanges.length) {
                            String allDataValue = "";
                            for (int i = 0; i < dataNames.length; i++) {
                                String[] byteRange = dataRanges[i].split("-");
                                if(byteRange.length==2) {
                                    int byteStart = Integer.parseInt(byteRange[0]);
                                    int byteEnd = Integer.parseInt(byteRange[1]);
                                    String realHexStr = hexStr.substring(device.getNodeId().length());
                                    long longValue = HexUtils.hexRangeToDecimal(realHexStr, byteStart, byteEnd);
                                    String realSValue = "";
                                    if(device.getName().contains("温湿")) {
                                        double doubleValue = (double) longValue / 10 ;
                                        realSValue = String.format("%.1f", doubleValue);
                                    }
                                    else if(device.getName().contains("PH")) {
                                        double doubleValue = (double) longValue / 100 ;
                                          realSValue = String.format("%.2f", doubleValue);
                                    }
                                    else {
                                        realSValue = String.format("%d", longValue);
                                    }
                                    if (i==0) {
                                        allDataValue = realSValue;
                                    }
                                    else {
                                        allDataValue = allDataValue + "|" + realSValue;
                                    }
                                    //如果超过阈值则发送报警邮件，否则取消报警状态
                                    String[] thresholdRange = thresholds[i].split("-");
                                    if(thresholdRange.length==2) {
                                        if(Double.parseDouble(realSValue)<Double.parseDouble(thresholdRange[0]) || Double.parseDouble(realSValue)>Double.parseDouble(thresholdRange[1])) {
                                            if("0".equals(device.getAlarmStatus())) {
                                                String alarmContent = device.getAlarm();
                                                String[] toMailAccountList = deviceService.getDeviceUserMails(device.getId());
                                                if(toMailAccountList != null) {
                                                    //编号为：${id}的传感器设备：${name}，发生${dataName}报警事件，阈值为：${threshold}，当前值：${dataValue}。
                                                    alarmContent = alarmContent.replaceAll("\\$\\{id\\}", device.getId().toString());
                                                    alarmContent = alarmContent.replaceAll("\\$\\{name\\}", device.getName());
                                                    alarmContent = alarmContent.replaceAll("\\$\\{dataName\\}", dataNames[i]);
                                                    alarmContent = alarmContent.replaceAll("\\$\\{threshold\\}", thresholds[i]);
                                                    alarmContent = alarmContent.replaceAll("\\$\\{dataValue\\}", realSValue);
                                                    MailUtil.sendMail(MailUtil.MyEmailAccount, MailUtil.MyEmailPassword, "",
                                                            toMailAccountList, "", MailUtil.MyEmailSMTPHost,
                                                            "物联网设备报警邮件", alarmContent);
                                                    deviceService.saveDeviceAlarmStatus(device.getId(), "1");
                                                }
                                            }
                                            deviceService.updateDeviceStatus("1", device.getId());
                                        }
                                        else {
                                            if("1".equals(device.getAlarmStatus())) {
                                                deviceService.saveDeviceAlarmStatus(device.getId(), "0");
                                            }
                                            deviceService.updateDeviceStatus("1", device.getId());
                                        }
                                    }
                                    else {
                                        deviceService.updateDeviceStatus("0", device.getId());
                                    }
                                }
                                else {
                                    deviceService.updateDeviceStatus("0", device.getId());
                                }
                            }
                            log.info(String.valueOf(device));
                            Long id = device.getId();
                            String code = device.getProjectCode();
                            String point = device.getAcquisitionPoint();
                            String time = TimeUtil.getCurrentDateTime();
                            String dataName = device.getDataName();
                            String dataValue = allDataValue;
                            boolean b = deviceService.updateByICP(id,code,point,time,dataName,dataValue);
                            if (b){
                                deviceService.stopDevice(id);
                            }
                        }
                        else {
                            deviceService.updateDeviceStatus("0", device.getId());
                        }
                        break;
                    }
                }
                catch (Exception e) {
                    deviceService.updateDeviceStatus("0", device.getId());
                    e.printStackTrace();
                }
            }
        }

    }
}
