# 

* https://blog.51cto.com/u_16175451/6630240
* https://blog.csdn.net/weixin_42575806/article/details/131513847
* https://www.cnblogs.com/JackyGz/p/17952920

* [【基础】MQTT -- MQTT 特性：QoS、Retained 消息、LWT 以及 Keepalive](https://blog.csdn.net/zqf787351070/article/details/131331779)
* [物联网协议之MQTT源码分析(二)](https://blog.csdn.net/weixin_34185560/article/details/91457600)
* [【MQTT 5.0】协议 ——发布订阅模式、Qos、keepalive、连接认证、消息结构](https://blog.csdn.net/weixin_43764974/article/details/131277000)

```xml
    <!-- mqtt -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-integration</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.integration</groupId>
      <artifactId>spring-integration-stream</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.integration</groupId>
      <artifactId>spring-integration-mqtt</artifactId>
    </dependency>
```

```yml
mqtt:
  hostUrl: tcp://127.0.0.1:1883
  username: admin
  password: HelloWorld42@
  client-id: MQTT-CLIENT-DEV
  cleanSession: true
  reconnect: true
  timeout: 100
  keepAlive: 100
  defaultTopic: testtopic
  serverTopic: testtopic
  isOpen: true
  qos: 0

# 循环引用配值
spring:
  main:
    allow-circular-references: true
```

# Eclipse Paho MQTT会生成许多包含.lck文件的文件夹

```java
        // 使用内存持久化 去除.lck文件生成
        MqttAsyncClient mqttClient = new MqttAsyncClient(config.getHostUrl(), MqttProperties.CLIENT_ID, new MemoryPersistence());
```
