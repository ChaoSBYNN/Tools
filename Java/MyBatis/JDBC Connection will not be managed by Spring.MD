# JDBC Connection will not be managed by Spring


## 日志打印

```log
JDBC Connection [HikariProxyConnection@1566755301 wrapping com.mysql.cj.jdbc.ConnectionImpl@1b263558] will not be managed by Spring
```

## 原因 : 未在对应方法上加@Transactional注解

加了`@Transactional` 注解后

```log
----
```
