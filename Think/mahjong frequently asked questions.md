### CardDown 牌组扩展

1. Player    add    CardDown
2. Machine    add    CardDown
3. PlayerService    extends
4. MachineService    etends
5. GameLogicProcessor    	@Qualifier("playerService")    @Qualifier("machineService")
6. AsyncLogicHandleThread    	@Qualifier("playerService")    	@Qualifier("machineService")

### 房间金币

1. config_db    global_congif
2. hubei_heji_xt_db    t_global

### 分支

1. create feature/some_xxx
2. push to origin feature/some_xxx
3. origin feature/some_xxx to pull request develop ,and delete feature/some_xxx

### 方法名一致性 （1新老框架）（2子类父类）

### pom 文件

1. project artifactId
2. manifest mainClass

### 测试打包流程

119.29.222.68
1. /data1/home/genghaoliang/RoomProxy_test2/conf
vi proxy.conf

2. /data1/home/genghaoliang/RoomProxy_test2/bin
sa.sh && sp.sh

115.159.48.163

/data1/games/xiantao
1. config
2. lib
3. service.sh
4. xiantao-mahjong-server-1.0.0-SNAPSHOT.jar

### 支付&代理接入

1. server.xml 端口号
2. config.xml
  <entry key="gameSocketPort">17764</entry>
  <entry key="logicGameId">327941</entry>
3. catalina.sh Java 内存限制

### 新手练习

SystemConfig.mjType =>  GAME_PLAY_SOME_RULE
SystemConfig.mjType 父类存在默认值

### 常用调用

XiantaoGameLogicProcessor
0. createVIPTable
1. sendCards
2. notifyNextPlayerOperation
3. player_chu_notify
4. playingTableTick
5. gameTableTick

XiantaoAsyncLogicHandleThread
0. execute_operation_msg
1. playerChuOperation
2. player_chu_notify
3. player_op_peng
4. player_op_gang
5. real_player_op_gang
6. player_op_hu
7. auto_hu_other_player
8. player_hu
9. win_lose_gold_calculate_hu

XiantaoMahjongProcessor
0. check_chi
1. check_peng
2. check_gang_with_hun
3. checkWin
4. doCheckWin
