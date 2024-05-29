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
