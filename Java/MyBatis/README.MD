# MyBatis

## mappr.xml src 同级目录生效

```xml

    <build>
        ...
        <resources>
            <!-- 扫描src/main/java下所有xx.xml文件 -->
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <!-- 扫描resources下所有资源 -->
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
    </build>
```

## 更新操作字段允许为空

```java
@TableField(updateStrategy = FieldStrategy.ALWAYS)
```


## mybatis-plus日志设置只打印sql，不打印结果集
```yaml
# 日志配置
mybatis-plus:
mapper-locations: classpath:com/gblfy/**/mapping/.xml
configuration:
#log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 注：把log-impl注释掉
```

项目中使用了mybatis-plus，又是spring boot工程，修改配置就可以了，具体操作为：
step1：注释掉yml中mybatis-plus configuration log-impl输出到控制台的配置；

```yaml
#  configuration:
#    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
#    map-underscore-to-camel-case: true
```

step2：在yml中增加log4j日志的配置

```yaml
logging:
  level:
    com.xxx.mapper: debug
```

修改后再次查看sql打印情况，发现只有sql语句、参数、结果集中数据的数量被打印出了，效果达到
