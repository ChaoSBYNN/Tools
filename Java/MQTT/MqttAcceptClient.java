
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description : MQTT接受服务的客户端
 * @Author : Sherlock
 * @Date : 2023/8/1 16:26
 */
@Component
public class MqttAcceptClient {

    private static final Logger logger = LoggerFactory.getLogger(MqttAcceptClient.class);

    @Autowired
    private MqttAcceptCallback  mqttAcceptCallback;

    @Autowired
    private MqttProperties      mqttProperties;

    public static MqttClient    client;

    private static MqttClient getClient() {
        return client;
    }

    private static void setClient(MqttClient client) {
        MqttAcceptClient.client = client;
    }

    /**
     * 客户端连接
     */
    public void connect() {
        MqttClient client;
        try {
            client = new MqttClient(mqttProperties.getHostUrl(), mqttProperties.getClientId(),
                new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setConnectionTimeout(mqttProperties.getTimeout());
            options.setKeepAliveInterval(mqttProperties.getKeepAlive());
            options.setAutomaticReconnect(mqttProperties.getReconnect());
            options.setCleanSession(mqttProperties.getCleanSession());
            MqttAcceptClient.setClient(client);
            // 设置回调
            client.setCallback(mqttAcceptCallback);
            client.connect(options);
        } catch (Exception e) {
            logger.error("MqttAcceptClient connect error,message:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 重新连接
     */
    public void reconnection() {
        try {
            client.connect();
        } catch (MqttException e) {
            logger.error("MqttAcceptClient reconnection error,message:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 订阅某个主题
     *
     * @param topic 主题
     * @param qos   连接方式
     */
    public void subscribe(String topic, int qos) {
        logger.info("========================【开始订阅主题:" + topic + "】========================");
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            logger.error("MqttAcceptClient subscribe error,message:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 取消订阅某个主题
     *
     * @param topic
     */
    public void unsubscribe(String topic) {
        logger.info("========================【取消订阅主题:" + topic + "】========================");
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            logger.error("MqttAcceptClient unsubscribe error,message:{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
