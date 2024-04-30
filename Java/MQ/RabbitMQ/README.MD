# Rabbitmq

## rabbitmq setMessageListener 导致@RabbitListener失效

### 问题解释：

> 在使用RabbitMQ时，如果你同时设置了setMessageListener方法和@RabbitListener注解，可能会导致@RabbitListener失效。这是因为当你使用setMessageListener方法时，你将一个自定义的MessageListener设置到了RabbitMQ的消费者上，这会覆盖掉Spring Boot中通过@RabbitListener注解配置的消息监听器。

### 解决方法：

要解决这个问题，你需要决定是使用@RabbitListener注解还是使用MessageListener接口。不能两者同时使用，因为它们都是用来定义消息监听器的方式，但是Spring只能处理一种。
如果你想使用@RabbitListener注解，那么不要使用setMessageListener方法。直接在你的方法上使用@RabbitListener注解，并通过Spring的自动装配机制注入所需的依赖，如RabbitTemplate等。

```java
@Component
@RabbitListener(queues = "yourQueueName")
public class YourConsumer {
    @RabbitHandler
    public void processMessage(YourMessageType message) {
        // 处理消息的逻辑
    }
}
```

如果你需要自定义消息处理逻辑并且想要保持使用setMessageListener，那么你应该创建一个新的MessageListener并在其中调用你想要执行的逻辑。

```java
public class CustomMessageListener implements MessageListener {
    private final YourCustomProcessor processor;
 
    public CustomMessageListener(YourCustomProcessor processor) {
        this.processor = processor;
    }
 
    @Override
    public void onMessage(Message message) {
        // 调用你的处理器方法
        processor.processMessage(message);
    }
}
 
@Component
public class YourCustomProcessor {
    public void processMessage(Message message) {
        // 处理消息的逻辑
    }
}
```

然后，你可以在你的RabbitMQ连接代码中使用这个自定义的MessageListener：

```java
CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
// 设置你的RabbitMQ连接信息
connectionFactory.setHost("localhost");
connectionFactory.setUsername("guest");
connectionFactory.setPassword("guest");
 
// 创建队列
Queue queue = new Queue("yourQueueName", true);
 
// 创建队列管理器
RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
rabbitAdmin.declareQueue(queue);
 
// 创建消费者
SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
container.setConnectionFactory(connectionFactory);
container.setQueueNames("yourQueueName");
 
// 设置自定义的MessageListener
CustomMessageListener customListener = new CustomMessageListener(/* 传入处理器实例 */);
container.setMessageListener(customListener);
 
// 启动监听器
container.start();
```

确保在项目关闭时正确关闭容器，以避免资源泄露。

最终，你应该选择一种方法并遵循相应的实践，以确保你的RabbitMQ消息监听器按预期工作。
