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
