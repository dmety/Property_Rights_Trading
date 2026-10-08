package cn.edu.cjxy.iotlink.mqtt;

import cn.edu.cjxy.iotlink.config.MqttConfig;
import cn.edu.cjxy.iotlink.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MqttFactory {
    public static ConcurrentHashMap<String, MqttClient> clientMap = new ConcurrentHashMap<>();

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MqttConfig mqttConfig;

    /**
     * 在bean初始化后连接到服务器
     */
    @PostConstruct
    public void init() {
        // 初始化订阅主题
        initSubscribeTopic(getInstance());
    }

    /**
     * 初始化客户端
     */
    public MqttClient getInstance() {
        MqttClient client = null;
        if (clientMap.get(mqttConfig.getClientId()) == null) {
            try {
                client = new MqttClient(mqttConfig.getHostUrl(), mqttConfig.getClientId());
                // MQTT配置对象
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                //设置自动重连, 其它具体参数可以查看MqttConnectOptions
                mqttConnectOptions.setAutomaticReconnect(true);
                //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
                //mqttConnectOptions.setCleanSession(true);
                //设置超时时间单位为秒
                mqttConnectOptions.setConnectionTimeout(30);
                mqttConnectOptions.setUserName(mqttConfig.getUserName());
                mqttConnectOptions.setPassword(mqttConfig.getPassWord().toCharArray());
                // 设置会话心跳时间 单位为秒
                mqttConnectOptions.setKeepAliveInterval(10);
                if (!client.isConnected()) {
                    client.connect(mqttConnectOptions);
                }
                client.setCallback(new MqttCallBack());
                log.info("MQTT创建client成功={}", JSONObject.toJSONString(client));
                clientMap.put(mqttConfig.getClientId(), client);
            }
            catch (MqttException e) {
                log.error("MQTT连接消息服务器[{}]失败", mqttConfig.getClientId() + "-" + mqttConfig.getHostUrl());
            }
        }
        else {
            client = clientMap.get(mqttConfig.getClientId());
            log.info("MQTT从map里获取到client，clientId=" + mqttConfig.getClientId());
        }
        return client;
    }

    /**
     * 初始化订阅主题
     * <p>
     * 消息等级，和主题数组一一对应，服务端将按照指定等级给订阅了主题的客户端推送消息
     */
    public void initSubscribeTopic(MqttClient client) {
        // 订阅设备发布消息主题
        try {
            client.subscribe(mqttConfig.getDevicePubTopic(), 2,
                    new MqttMessageReciver(mqttConfig.getDevicePubTopic(), deviceService));
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }


}


