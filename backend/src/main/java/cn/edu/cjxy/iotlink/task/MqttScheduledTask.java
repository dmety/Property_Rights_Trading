//package cn.edu.cjxy.iotlink.task;
//
//import cn.edu.cjxy.iotlink.config.MqttConfig;
//import cn.edu.cjxy.iotlink.model.SysDevice;
//import cn.edu.cjxy.iotlink.mqtt.MqttFactory;
//import cn.edu.cjxy.iotlink.service.DeviceService;
//import cn.edu.cjxy.iotlink.util.HexUtils;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MqttScheduledTask {
//    @Autowired
//    DeviceService deviceService;
//
//    @Autowired
//    private MqttConfig mqttConfig;
//
//    @Autowired
//    MqttFactory mqttFactory;
//
//    // CRON 表达式方式（每10秒执行一次）
//    @Scheduled(cron = "*/10 * * * * ?")
//    public void runTaskEvery5Second() {
//        sendCollectCommand(1L);
//    }
//
//    // 每分钟执行一次
//    @Scheduled(cron = "0 * * * * ?")
//    public void runTaskEveryMinute() {
//        sendCollectCommand(2L);
//    }
//
//    private void sendCollectCommand(Long taskId) {
//        MqttClient client = mqttFactory.getInstance();
//        List<SysDevice> deviceList = deviceService.getDeviceListByTaskId(taskId);
//        for (SysDevice device: deviceList) {
//            try {
//                String collectCmd = device.getCollectCmd();
//                String nodeId = device.getNodeId();
//                if(collectCmd != null && !collectCmd.isEmpty()) {
//                    String[] hexStrArray = collectCmd.split("\\|");
//                    for (String hexStr : hexStrArray) {
//                        byte[] hexData = HexUtils.hexStringToByteArray(nodeId + hexStr);
//                        MqttMessage message = new MqttMessage(hexData);
//                        String deviceSubTopic = mqttConfig.getDeviceSubTopic();//下发命令到LORA网关订阅主题
//                        client.publish(deviceSubTopic, message);
//                        Thread.sleep(1000);
//                    }
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
//
