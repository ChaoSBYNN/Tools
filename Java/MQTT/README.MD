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
