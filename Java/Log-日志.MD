# Log 日志

## 常用日志

1. JDK日志
2. log4j系列
3. logbak系列

### JDK日志 (java.util.logging=jul)

> 从jdk1.4起，JDK开始自带一套日志系统。JDK Logger最大的优点就是不需要任何类库的支持，只要有Java的运行环境就可以使用。相对于其他的日志框架，JDK自带的日志可谓是鸡肋，无论易用性，功能还是扩展性都要稍逊一筹，所以在商业系统中很少直接使用。

JDK默认的logging配置文件为：

```bash
$JAVA_HOME/jre/lib/logging.properties
```

可以使用系统属性java.util.logging.config.file指定相应的配置文件对默认的配置文件进行覆盖，比如， 
```cmd
java -Djava.util.logging.config.file=myfile
```

JDK Logging 把日志分为如下七个级别，等级依次降低。

|级别|SERVER|WARNING|INFO|CONFIG|FINE|FINER|FINEST|
|---|---|---|---|---|---|---|---|
|调用方法|server()|warning()|info()|config()|fine()|finer|finest|
|含义|严重|警告|信息|配置|良好|较好|最好|
