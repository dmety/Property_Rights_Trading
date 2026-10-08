package cn.edu.cjxy.iotlink.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MqttCallBack implements MqttCallback {
    /**
     * 客户端断开连接的回调
     */
    @Override
    public void connectionLost(Throwable cause) {
        log.info("客户端断开连接回调:" + cause.getMessage());
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {

    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        IMqttAsyncClient client = token.getClient();
        log.info(client.getClientId() + "发布消息成功！");
    }
}


