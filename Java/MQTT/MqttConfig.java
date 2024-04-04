
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Description : 启动服务的时候开启监听客户端
 * @Author : Sherlock
 * @Date : 2023/8/1 16:35
 */
@Configuration
public class MqttConfig {

    @Autowired
    private MqttAcceptClient mqttAcceptClient;

    /**
     * 订阅mqtt
     *
     * @return
     */
    @Conditional(MqttCondition.class)
    @Bean
    public MqttAcceptClient getMqttPushClient() {
        mqttAcceptClient.connect();
        return mqttAcceptClient;
    }
}
