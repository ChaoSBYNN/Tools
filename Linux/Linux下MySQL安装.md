---
title: Linux 下MySQL安装
date: 2017-02-27 19:05:36
tags: Linux
---

> 注意：如果安装失败，可尝试关闭selinux和防火墙再行测试。

### 安装MySQL前准备

##### 第一步：安装编译源码所需的工具和库 

```bash
	yum install gcc gcc-c++ ncurses-devel perl 
```

##### 第二步：mysql 5.6版本之后需要cmake,安装cmake

```bash
	wget http://www.cmake.org/files/v2.8/cmake-2.8.10.2.tar.gz   
	tar -xzvf cmake-2.8.10.2.tar.gz   
	cd cmake-2.8.10.2   
	./bootstrap ; make ; make install   
```

### 安装MySQL

##### 第一步：新建mysql用户和mysql组

新增mysql用户组 

```bash
	groupadd mysql 
```

新增mysql用户 

```bash
	useradd -r -g mysql mysql 
```

 ![](/images/2017_02_27_1.jpg)

##### 第二步：新建MySQL所需要的目录

新建mysql安装目录 

```bash
	mkdir -pv  /usr/local/mysql
```

>建立mysql的安装目录，建议先创建目录
>若不建立可能在编译安装的时候报no such irectory

新建mysql数据库数据文件目录

```bash
	mkdir -p /data/mysqldb  
```

指定mysql的数据文件的目录，建议编译安装前先创建此目录

##### 第三步：下载MySQL并解压

```bash
	wget http://dev.mysql.com/get/Downloads/MySQL-5.6/mysql-5.6.24.tar.gz
	tar -zxvf mysql-5.6.14.tar.gz
```

##### 第四步：安装MySQL

切换到 mysql-5.6.24  

```bash
	cd mysql-5.6.24
```

编译mysql，执行如下命令：

```bash
	cmake \
	-DCMAKE_INSTALL_PREFIX=/usr/local/mysql \
	-DMYSQL_DATADIR=/usr/local/mysql/data \
	-DSYSCONFDIR=/etc \
	-DWITH_MYISAM_STORAGE_ENGINE=1 \
	-DWITH_INNOBASE_STORAGE_ENGINE=1 \
	-DWITH_MEMORY_STORAGE_ENGINE=1 \
	-DWITH_READLINE=1 \
	-DMYSQL_UNIX_ADDR=/var/lib/mysql/mysql.sock \
	-DMYSQL_TCP_PORT=3306 \
	-DENABLED_LOCAL_INFILE=1 \
	-DWITH_PARTITION_STORAGE_ENGINE=1 \
	-DEXTRA_CHARSETS=all \
	-DDEFAULT_CHARSET=utf8 \
	-DDEFAULT_COLLATION=utf8_general_ci
```

接着使用 make && make install 来编译并安装
 
安装完毕的截图如下

 ![](/images/2017_02_27_2.jpg) 

### 安装MySQL后设置

##### 第一步：修改mysql目录属组

修改mysql安装目录
>若不修改默认的是root用户root组，启动会报错
>（Starting MySQL..The server quit without updating PID file [FAILED]/mysql/Server03.mylinux.com.pid）

```bash
	cd /usr/local/mysql   
	chown -R mysql:mysql . 
```

修改mysql数据库文件目录

```bash
	cd /data/mysqldb  
	chown -R mysql:mysql .  
```

##### 第二步：初始化mysql数据库

```bash
	cd /usr/local/mysql   
```

以mysql的身份初始化

```bash
	scripts/mysql_install_db --user=mysql --datadir=/data/mysqldb 
```

##### 第三步：复制mysql服务启动配置文件

```bash
	cp /usr/local/mysql/support-files/my-default.cnf /etc/my.cnf  
```

> 注：如果/etc/my.cnf文件存在，则覆盖。

##### 第四步：复制mysql服务启动脚本及加入PATH路径

>（不用每次都要到安装目录执行mysql）

```bash
	cp support-files/mysql.server /etc/init.d/mysqld     
	vim /etc/profile   
	PATH=/usr/local/mysql/bin:/usr/local/mysql/lib:$PATH  
	export PATH  
	source /etc/profile    
```

##### 第五步： 启动mysql服务并加入开机自启动(可选这个步骤，以后可以自己启动的)

```bash
	service mysqld start 
	chkconfig --level 35 mysqld on
```

 ![](/images/2017_02_27_3.jpg) 

##### 第六步：检查mysql服务是否启动

```bash
	netstat -tulnp | grep 3306   
```

 ![](/images/2017_02_27_4.jpg)
 
到此服务器mysql已经安装并已经启动，接下来修改MySQL用户root的密码，登录测试

```bash
	mysqladmin -u root password '123456'  
```

##### 第七步：mysql命令登录测试

 ![](/images/2017_02_27_5.jpg) 

mysql安装完毕，成功登录
