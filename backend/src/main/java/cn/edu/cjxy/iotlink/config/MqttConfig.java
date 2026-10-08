package cn.edu.cjxy.iotlink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT(Message Queuing Telemetry Transport, 消息队列遥测传输协议)，
 * 是一种由IBM在1999年构建于TCP/IP协议上的基于发布/订阅(publish/subscribe)模式的轻量级开放式通信协议。
 * MQTT可以以极少的代码和有限的带宽，为远程连接设备提过实时可靠的消息服务。
 * 作为一种低开销、低带宽占用的即时通讯协议，使其在物联网、小型设备、移动应用等方面有较广泛的应用。
 * 在很多情况下，包括受限的环境中，如:机器与机器（M2M）通信和物联网(loT)。
 * 其在通过卫星链路通信传感器、偶尔拨号的医疗设备、智能家居、以及一些小型化设备中已广泛使用
 *
 * MQTT特点：
 * 使用发布/订阅消息模式，提供一对多的消息发布，解除应用程序耦合；
 * 对负载内容屏蔽的消息传输；
 * 使用 TCP/IP 提供网络连接；
 * 有三种消息发布服务质量：
 * 小型传输，开销很小（固定长度的头部是2字节），协议交换最小化，以降低网络流量；
 * 使用LastWill和Testament特性通知有关各方客户端异常中断的机制。
 */
@Configuration
@ConfigurationProperties(prefix="mqtt")
@Data
public class MqttConfig {
    private String userName;
    private String passWord;
    private String hostUrl;
    private String clientId;
    private String devicePubTopic;//从机回传数据
    private String deviceSubTopic;//服务下发命令
}


