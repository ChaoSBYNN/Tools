# Spring Boot + RabbitMQ 从入门到架构级应用

## 一、核心概念

### 1.1 一句话本质
RabbitMQ是基于**AMQP协议**的消息代理，核心职责是：**接收消息→路由→存储→投递**。

**关键细节**：
- **协议层**：AMQP是应用层协议，默认端口5672（AMQP）/15672（Management UI）
- **进程模型**：Erlang编写的OTP应用，采用Actor模型，进程间通过消息传递通信
- **默认交换机**：空字符串`""`表示默认交换机，绑定到所有队列，routing key等于队列名

### 1.2 消息流转模型
```
Producer → Connection → Channel → Exchange → Binding → Queue → Channel → Consumer
```

- **Channel不是线程安全的**：一个Channel不应该被多个线程共享，这也是Spring使用Channel池的原因
- **Connection复用**：TCP连接昂贵，但Channel是轻量级（基于Erlang进程），一个Connection可承载数千个Channel

### 1.3 Exchange类型

| 类型 | 路由规则 | 使用场景 | 特殊说明 |
|------|---------|---------|---------|
| **Direct** | 精确匹配routing key | 点对点任务分发 | 默认交换机等价于direct |
| **Topic** | 模式匹配（`*`匹配一个单词，`#`匹配零或多个） | 日志分类、新闻订阅 | 最大255字节，点号分隔 |
| **Fanout** | 忽略routing key，广播 | 多副本缓存更新 | 性能最好，无需匹配计算 |
| **Headers** | 匹配headers键值对 | 复杂属性路由 | `x-match: all/any`控制与或逻辑 |
| **Consistent Hash** | 哈希路由（需插件） | 队列分片、负载均衡 | 插件：`rabbitmq_consistent_hash_exchange` |

---

## 二、Spring Boot集成深度解析

### 2.1 自动配置原理
Spring Boot通过`RabbitAutoConfiguration`自动注入：
- `CachingConnectionFactory`：带缓存的Connection工厂（缓存Channel和Connection）
- `RabbitTemplate`：发送模板
- `RabbitMessagingTemplate`：集成Spring Messaging的抽象
- `SimpleRabbitListenerContainerFactory`：监听容器工厂

**关键配置**：
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    # 连接池配置（关键补充）
    cache:
      channel:
        size: 25          # 缓存Channel数
        checkout-timeout: 10000ms
    listener:
      simple:
        acknowledge-mode: auto
        prefetch: 1       # 全局QoS，防止单个消费者积压
        concurrency: 5    # 最小并发消费者
        max-concurrency: 10
        default-requeue-rejected: false  # 拒绝后不入队，直接进入死信
        retry:
          enabled: true
          max-attempts: 3
          initial-interval: 1000ms
          multiplier: 2
```

### 2.2 完整配置类示例（动态队列、TTL、死信）

```java
@Configuration
public class AdvancedRabbitConfig {
    
    // 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("order.dlx.exchange", true, false);
    }
    
    // 死信队列
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable("order.dlx.queue").build();
    }
    
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("order.fail");
    }
    
    // 业务队列（带死信、TTL、持久化）
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable("order.queue")
                // 死信交换机
                .withArgument("x-dead-letter-exchange", "order.dlx.exchange")
                // 死信routing key
                .withArgument("x-dead-letter-routing-key", "order.fail")
                // 消息TTL（30秒，可选）
                .withArgument("x-message-ttl", 30000)
                // 队列TTL（1小时无消费者自动删除，可选）
                .withArgument("x-expires", 3600000)
                // 队列最大长度（防止内存爆炸）
                .withArgument("x-max-length", 100000)
                // 溢出行为：reject-publish或drop-head
                .withArgument("x-overflow", "reject-publish")
                .build();
    }
    
    // 消息转换器（JSON，必须配置，原文缺失）
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, 
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        
        // 强制确认（mandatory=true，路由失败回调）
        template.setMandatory(true);
        // 确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息到达交换机失败: {}", cause);
                // 补偿逻辑：入库定时重试
            }
        });
        // 回退回调（路由到队列失败）
        template.setReturnsCallback(returned -> {
            log.error("消息路由到队列失败: exchange={}, routingKey={}, msg={}",
                returned.getExchange(), 
                returned.getRoutingKey(),
                new String(returned.getMessage().getBody()));
        });
        return template;
    }
}
```

### 2.3 消费端高级用法（@RabbitHandler、多队列）

```java
@Component
@RabbitListener(queues = {"order.queue", "pay.queue"}, 
                containerFactory = "batchListenerContainerFactory")
public class OrderConsumer {
    
    // 区分不同消息类型（多态处理）
    @RabbitHandler
    public void handleOrder(OrderMessage msg, Channel channel, 
                           @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            processOrder(msg);
            // 手动确认（multiple=false，不批量确认）
            channel.basicAck(tag, false);
        } catch (BusinessException e) {
            // 业务异常：直接丢弃或进死信（不重试）
            channel.basicNack(tag, false, false);
        } catch (Exception e) {
            // 系统异常：重试（若已到重试次数，Spring会调用RecoveryCallback）
            throw new AmqpRejectAndDontRequeueException("消费失败，进入死信", e);
        }
    }
    
    // 监听死信队列（人工介入）
    @RabbitListener(queues = "order.dlx.queue")
    public void handleDeadLetter(Message msg, Channel channel) {
        // 记录到DB或发送告警
        saveToDeadLetterDb(msg);
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
    }
}
```

---

## 三、ACK机制深度澄清

### 3.1 三种ACK模式详解

| 模式 | 机制 | 风险点 | 适用场景 |
|------|------|--------|----------|
| **NONE** | 自动确认（Auto Ack），消息投递后立即从队列删除 | **消息丢失风险最高**，消费者处理失败消息已丢失 | 容忍丢失的日志、监控数据 |
| **AUTO** | 方法正常返回则ack，抛异常则nack（根据requeue配置） | 异常无限循环（若requeue=true） | 默认推荐，配合重试策略 |
| **MANUAL** | 代码显式控制ack/nack | 忘ack导致unacked堆积；幂等性需自己保证 | 金融交易、对可靠性要求极高 |

NONE模式不是"容器不发ack"，而是**broker自动视为已确认**，不等业务处理完成。

### 3.2 Nack vs Reject（原文可补充）
- **basicNack(multiple=true)**：可批量拒绝（delivery tag之前的所有消息）
- **basicReject()**：只能拒绝单条，不支持multiple
- **Requeue决策**：
  - `requeue=true`：消息回到队列头部（注意：如果是单消费者且代码有bug，会导致无限循环）
  - `requeue=false`：若配置了DLX则进入死信，否则丢弃

---

## 四、可靠性保障体系

confirm和return完整方案：

### 4.1 生产者可靠性（三层保障）

1. **Confirm机制**（确保到达Exchange）
   ```java
   // 开启confirm模式（YAML配置）
   spring.rabbitmq.publisher-confirm-type: correlated
   // 或 simple（旧版兼容，性能略差）
   ```

2. **Return机制**（确保路由到Queue）
   ```java
   spring.rabbitmq.publisher-returns: true
   template.setMandatory(true);  // 必须同时设置
   ```

3. **本地消息表（最终一致性）**
   对于金融场景，必须落库+定时扫描补偿：
   ```java
   @Transactional
   public void createOrder(Order order) {
       orderMapper.insert(order);
       // 插入消息记录（状态：待发送）
       msgLogMapper.insert(new MessageLog(order.getId(), "order.exchange", "order.create"));
       
       rabbitTemplate.convertAndSend("order.exchange", "order.create", order, 
           correlationData -> {
               correlationData.setId(order.getId().toString());
               return correlationData;
           });
   }
   
   // 定时任务扫描未确认消息重发
   @Scheduled(fixedDelay = 60000)
   public void resendFailedMessages() {
       List<MessageLog> failedMsgs = msgLogMapper.selectByStatus("SEND_FAIL");
       failedMsgs.forEach(msg -> {
           // 重试次数+1，超过阈值则人工介入
           rabbitTemplate.convertAndSend(msg.getExchange(), msg.getRoutingKey(), msg.getBody());
       });
   }
   ```

### 4.2 消费者可靠性
- **幂等性设计**：使用**业务唯一ID**（如订单号）去重，或数据库唯一索引
- **消费确认**：业务操作和ACK必须在同一事务边界内（或先业务后ACK，避免业务失败但已ACK）

---

## 五、性能调优与架构设计

### 5.1 Prefetch与Concurrency黄金法则

**误区纠正**：不是concurrency越大越好，也不是prefetch越小越好。

**计算公式**：
```
最优prefetch = (单个消息处理时间 / 期望吞吐量) × 消费者数
```

**推荐配置**：
- **快速任务**（<50ms）：`prefetch=50-100`, `concurrency=10-20`
- **慢速任务**（>1s，如调用第三方API）：`prefetch=1-5`, `concurrency=5-10`（防止单消费者堆积）

**原理**：RabbitMQ采用**推模式**，broker会尽可能推送prefetch数量的消息到消费者内存，处理慢时会造成内存压力。

### 5.2 队列分片（补充）
单队列是并发瓶颈（Erlang进程单线程执行），高并发场景使用**Consistent Hash Exchange**：
```java
// 创建10个队列分片
for (int i = 0; i < 10; i++) {
    Queue queue = QueueBuilder.durable("order.queue." + i).build();
    admin.declareQueue(queue);
    // 绑定到hash exchange，routing key任意（会被hash）
    Binding binding = BindingBuilder.bind(queue).to(hashExchange()).with(String.valueOf(i));
    admin.declareBinding(binding);
}
```

### 5.3 延迟队列
RabbitMQ本身无延迟队列，需用**死信+TTL**或**延迟队列插件**：

**方案对比**：
- **DLX+TTL**：精度差（依赖队列头部消息TTL），适合固定延迟（如30分钟未支付取消）
- **Delayed Message Plugin**：精度高，支持任意延迟，但性能略差，且为社区插件

---

## 六、监控与运维

### 6.1 关键监控指标（metric）

| 指标 | 告警阈值 | 含义 |
|------|---------|------|
| `ready` | > 10000 | 消息堆积，消费能力不足 |
| `unacked` | > 1000 | 消费卡死或忘ack |
| `consumer_utilisation` | < 0.6 | 消费者利用率低，可能阻塞在IO |
| `memory_alarm` | true | 节点内存告警，将阻塞发布 |
| `disk_free_alarm` | true | 磁盘告警 |

### 6.2 常用命令
```bash
# 查看队列状态
rabbitmqctl list_queues name messages_ready messages_unacknowledged consumers

# 查看连接
rabbitmqctl list_connections peer_host peer_port state channels

# 清理单条消息（死信队列中错误消息）
rabbitmqadmin delete queue name=dlx.queue
```

---

## 七、常见误区纠正（原文基础上扩充）

| 误区 | 正确理解 |
|------|---------|
| **Direct Exchange只能绑定一个Queue** | 可以绑定多个Queue，routing key相同则广播 |
| **消息持久化=队列durable** | 需要三者同时设置：队列durable、消息deliveryMode=2、Exchange durable |
| **消费者挂了消息会丢失** | 只要未ACK且非auto-ack模式，消息会重回队列 |
| **RabbitMQ能保证严格顺序** | 单队列单消费者才能保证全局顺序，多分片或并发消费会乱序 |
| **TTL设置越小越好** | 队列TTL（x-message-ttl）检查有开销，大量短TTL消息影响性能 |

---

## 八、架构决策建议

### 8.1 何时选择RabbitMQ？
- ✅ 复杂路由需求（topic、headers）
- ✅ 需要延迟队列（DLX方案）
- ✅ 强一致性要求（ confirms + 事务消息）
- ❌ 超高吞吐量（>10万TPS）→ 考虑Kafka
- ❌ 流处理、消息回溯 → 考虑Kafka/Pulsar

### 8.2 队列命名规范建议
```
业务.动作.类型.版本
例如：order.create.normal.v1, order.create.delay.v1, order.create.dlx.v1
```

### 8.3 版本升级策略
- **Blue/Green部署**：消费者先升级，生产者后升级
- **消息兼容**：使用JSON Schema或Protobuf，新增字段保持向后兼容
