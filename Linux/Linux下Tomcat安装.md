---
title: Linux 下Tomcat安装
date: 2017-02-26 21:05:17
tags: Linux
---

### 第一步 下载tomcat

```bash
	wget http://apache.fayea.com/tomcat/tomcat-*/v*.*.**/bin/apache-tomcat-*.*.**.tar.gz
 ```
 或者在本地下载好tomcat安装包

![](/images/2017_02_26_01.jpg)

### 第二步 将tomcat移动放置到 /usr/local/ 目录下

```bash
	mv apache-tomcat-*.*.**.tar.gz /usr/local/
```
### 第三步 将tomcat解压缩

```bash
	tar -xvzf /usr/local/apache-tomcat-*.*.**.tar.gz
```

![](/images/2017_02_26_02.jpg)

### 第四步 设置tomcat开机自启动，

编辑/usr/local/apache-tomcat-*.*.**.tar.gz/bin/startup.sh

```bash
	vi /usr/local/apache-tomcat-*.*.**/bin/startup.sh
```

![](/images/2017_02_26_03.jpg)

添加

```bash
	#chkconfig: 2345 80 90
	#description:tomcat auto start
	#processname: tomcat
```

![](/images/2017_02_26_04.jpg)

编辑/usr/local/apache-tomcat-*.*.**/bin/catalina.sh

```bash
vi /usr/local/apache-tomcat-*.*.**/bin/catalina.sh
```

![](/images/2017_02_26_05.jpg)

搜索export关键字，加入如下行：

```bash
	export CATALINA_BASE=/usr/local/apache-tomcat-*.*.**
	export CATALINA_HOME=/usr/local/apache-tomcat-*.*.**
	export CATALINA_TMPDIR=/usr/local/apache-tomcat-*.*.**
```

![](/images/2017_02_26_06.jpg)

将tomcat加入开机自启动

```bash
	vi /etc/rc.d/rc.local
```

![](/images/2017_02_26_07.jpg)

加入如下内容：

```bash
	export JAVA_HOME=/usr/local/jdk*.*.*_**
	/usr/local/apache-tomcat-*.*.**/bin/startup.sh start
```

![](/images/2017_02_26_08.jpg)

### 第五步 测试Tomcat运行

执行

```bash
/usr/local/apache-tomcat-*.*.**/bin/startup.sh 
```

启动tomcat

打开浏览器测试：

![](/images/2017_02_26_09.jpg) 

执行

```bash
/usr/local/apache-tomcat-*.*.**/bin/shutdown.sh 
```

关闭tomcat
### 第六步 修改端口
tomcat默认监听8080端口，如果要修改成为80端口，按如下步骤修改：

```bash
vi /usr/local/apache-tomcat-*.*.**/conf/server.xml
```

将

```xml
	<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
	<Connector executor="tomcatThreadPool" port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
```

修改为：

```xml
	<Connector port="80" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
	<Connector executor="tomcatThreadPool" port="80" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
```

![](/images/2017_02_26_10.jpg)

重启tomcat生效。

测试Tomcat：

![](/images/2017_02_26_11.jpg)
 
tomcat安装完毕。
