---
title: Linux 下JDK安装
date: 2017-02-21 19:55:23
tags: Linux
---

> 注意：如果系统中存在JDK 或者 OpenJDK，先卸载。

### 第一步 下载JDK后解压

```bash
    tar -zxvf jdk-*u**-linux-x64.tar.gz  -C /dir
```

### 第二步 设置JAVA环境变量

```bash
   vim /root/.bash_profile
```

编辑文件，添加一下内容

```bash
    JAVA_HOME=/usr/local/src/jdk1.*.*_**
    export JAVA_HOME
    PATH=$PATH:$JAVA_HOME/bin
    export PATH
    CLASSPATH=.:$JAVA_HOME/lib
    export CLASSPATH

```

```bash
    vi /etc/profile
```

编辑相同内容

### 第三步 使环境变量生效

```bash
   source /root/.bash_profile
```

### 第四部 验证JAVA环境

```bash
   java –version
```
