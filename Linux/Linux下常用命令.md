```shell
	protoc.exe ./poker_game_interaction.proto --java_out=./
```

```shell
	netstat -apn | grep 17838
```

```shell
	kill -9
```

```shell
	ps -ef | grep java
```

```shell
	ps -aux | grep java
```

```shell
	tailf -3 proxy.conf
```

``` shell
	tail -f proxy.conf
```

```shell
	lsof -i:some-port-value
```

输出某文件尾信息 包含2.113 并且 不包含 "HandleHeartBeat" (-v 不包含)

```shell
	 tail -f gate_sys_svr.log.2017-06-07.0 | grep 2.113 | grep -v "HandleHeartBeat"
```
