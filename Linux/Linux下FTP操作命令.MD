# Linux下FTP操作命令

## 登录FTP服务器

> - ftp [IP] [PORT]
> - ftp [URL]

```shell
[root@ftpsrv vsftpd]# ftp 127.0.0.1 21
Connected to 127.0.0.1 (127.0.0.1).
220 (vsFTPd 2.2.2)
Name (127.0.0.1:root): nice
331 Please specify the password.
Password:
230 Login successful.
Remote system type is UNIX.
Using binary mode to transfer files.
ftp> 
```

## FTP 命令

### 帮助命令

```shell
ftp> ?|help                  # 显示ftp内部命令的帮助信息
```

### 查看命令

```shell
ftp> dir
227 Entering Passive Mode (127,0,0,1,44,241).
150 Here comes the directory listing.
drwxr-xr-x    2 501      501          4096 Sep 04 07:18 aaa
drwxr-xr-x    2 501      501          4096 Sep 05 08:59 bbb
226 Directory send OK.
ftp> ls
227 Entering Passive Mode (127,0,0,1,71,215).
150 Here comes the directory listing.
drwxr-xr-x    2 501      501          4096 Sep 04 07:18 aaa
drwxr-xr-x    2 501      501          4096 Sep 05 08:59 bbb
226 Directory send OK.
ftp> 
```

### 切换ftp的目录

```shell
ftp> cd aaa　　　　　　# 切换到ftp的aaa目录
250 Directory successfully changed.
ftp> pwd　　　　　　　　# 查看当前所在的ftp目录
257 "/aaa"
ftp> ls
227 Entering Passive Mode (127,0,0,1,88,202).
150 Here comes the directory listing.
-rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
-rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
226 Directory send OK.
ftp> 
```

### 下载上传

- 创建&删除目录
  ```shell
    ftp> mkdir ccc
    257 "/aaa/ccc" created
    ftp> ls
    227 Entering Passive Mode (127,0,0,1,194,152).
    150 Here comes the directory listing.
    -rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
    -rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
    drwxr-xr-x    2 501      501          4096 Sep 05 09:06 ccc
    226 Directory send OK.
    ftp> rmdir ccc
    250 Remove directory operation successful.
    ftp> ls
    227 Entering Passive Mode (127,0,0,1,214,114).
    150 Here comes the directory listing.
    -rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
    -rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
    226 Directory send OK.
  ```
- 下载文件
  ```shell
    ## 下载单个文件
    ftp> !pwd    　　　　# 查看当前所在的系统工作目录 
    /tmp
    ftp> ls 
    227 Entering Passive Mode (127,0,0,1,223,180).
    150 Here comes the directory listing.
    -rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
    -rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
    226 Directory send OK.
    ftp> get aaa1　　　　# 下载aaa1文件
    local: aaa1 remote: aaa1
    227 Entering Passive Mode (127,0,0,1,33,232).
    150 Opening BINARY mode data connection for aaa1 (0 bytes).
    226 Transfer complete.
    ftp> !ls
    aaa1
    ftp> get aaa2 aaa3　　　　# 下载aaa2文件，并命名为aaa3
    local: aaa3 remote: aaa2
    227 Entering Passive Mode (127,0,0,1,171,86).
    150 Opening BINARY mode data connection for aaa2 (0 bytes).
    226 Transfer complete.
    ftp> !ls　　　　　　　　　　# 查看，下载成功
    aaa1  aaa3

    ## 下载多个文件
    ftp> mget aaa1 aaa2
    mget aaa1? y
    227 Entering Passive Mode (127,0,0,1,83,114).
    150 Opening BINARY mode data connection for aaa1 (0 bytes).
    226 Transfer complete.
    mget aaa2? y
    227 Entering Passive Mode (127,0,0,1,217,121).
    150 Opening BINARY mode data connection for aaa2 (0 bytes).
    226 Transfer complete.
    ftp> !ls -l
    total 1532
    -rw-r--r-- 1 root root       0 Sep  5 17:20 aaa1　　　　# 可以看到文件创建时间已经更改
    -rw-r--r-- 1 root root       0 Sep  5 17:20 aaa2
    -rw-r--r-- 1 root root       0 Sep  5 17:14 aaa3
  ```
- 上传文件
  ```shell
    ## 创建文件
    [root@ftpsrv tmp]# touch ddd1
    [root@ftpsrv tmp]# touch ddd2
    [root@ftpsrv tmp]# ll
    total 1532
    -rw-r--r-- 1 root root       0 Sep  5 17:20 aaa1
    -rw-r--r-- 1 root root       0 Sep  5 17:20 aaa2
    -rw-r--r-- 1 root root       0 Sep  5 17:14 aaa3
    -rw-r--r-- 1 root root       0 Sep  5 17:23 ddd1
    -rw-r--r-- 1 root root       0 Sep  5 17:23 ddd2
    
    ## 上传
    ftp> put ddd1 　　　　　　# 上传单个文件，可以tab补全
    local: ddd1 remote: ddd1
    227 Entering Passive Mode (127,0,0,1,69,10).
    150 Ok to send data.
    226 Transfer complete.
    ftp> put ddd1 eee1　　　　# 上传单个文件并改名
    local: ddd1 remote: eee1
    227 Entering Passive Mode (127,0,0,1,243,160).
    150 Ok to send data.
    226 Transfer complete.
    ftp> ls
    227 Entering Passive Mode (127,0,0,1,234,62).
    150 Here comes the directory listing.
    -rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
    -rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
    drwxr-xr-x    2 501      501          4096 Sep 05 09:18 bbb
    -rw-r--r--    1 501      501             0 Sep 05 09:23 ddd1
    -rw-r--r--    1 501      501             0 Sep 05 09:23 eee1
    226 Directory send OK
    
    ## 上传多个文件
    ftp> mput ddd1 ddd2 
    mput ddd1? y
    227 Entering Passive Mode (127,0,0,1,92,11).
    150 Ok to send data.
    226 Transfer complete.
    mput ddd2? y
    227 Entering Passive Mode (127,0,0,1,66,108).
    150 Ok to send data.
    226 Transfer complete.
    ftp> ls
    227 Entering Passive Mode (127,0,0,1,191,91).
    150 Here comes the directory listing.
    -rw-r--r--    1 501      501             0 Sep 05 09:01 aaa1
    -rw-r--r--    1 501      501             0 Sep 05 09:02 aaa2
    drwxr-xr-x    2 501      501          4096 Sep 05 09:18 bbb
    -rw-r--r--    1 501      501             0 Sep 05 09:27 ddd1　　　　# 查看修改时间可以确认已经上传成功
    -rw-r--r--    1 501      501             0 Sep 05 09:27 ddd2
    -rw-r--r--    1 501      501             0 Sep 05 09:23 eee1
    226 Directory send OK.
  ```

## 文件目录操作

```shell
  ftp> delete aaa2　　　　# 删除aaa2文件
  ftp> mdelete ddd1 ddd2 eee1
  
  ftp> rmdir bbb　　　　# 删除bbb目录
```

## 其他命令

```shell
## 切换传输模式
ftp> ascii  　　　　　　# 切换为ascii模式
200 Switching to ASCII mode.
ftp> bin　　　　　　　　# 切换为二进制模式，默认登陆就是二进制传输模式
200 Switching to Binary mode.
```

```shell
## 关闭和重连ftp
ftp> close　　　　　　# 断开ftp的连接
221 Goodbye.
ftp> ls　　　　　　　　# 可以看出，无法显示ftp服务器的文件列表
Not connected.
ftp> open 127.0.0.1 21　　　　# 重新连接ftp服务器
Connected to 127.0.0.1 (127.0.0.1).
220 (vsFTPd 2.2.2)
Name (127.0.0.1:root): admin 　　　　# 使用admin登陆ftp
331 Please specify the password.
Password:
230 Login successful.
Remote system type is UNIX.
Using binary mode to transfer files.
ftp> ls　　　　　　　　# 可以再次查看ftp服务器的文件列表
227 Entering Passive Mode (127,0,0,1,227,247).
150 Here comes the directory listing.
drwxr-xr-x    2 501      501          4096 Sep 05 17:47 aaa
drwxr-xr-x    2 501      501          4096 Sep 05 16:59 bbb
226 Directory send OK.
```

```shell
## 退出ftp会话
ftp> quit　　　　# 也可以使用bye和exit
221 Goodbye.
[root@ftpsrv vsftpd]# 
```
