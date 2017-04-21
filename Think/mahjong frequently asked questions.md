### 分支使用

1. create feature/some_xxx
2. push to origin feature/some_xxx
3. origin feature/some_xxx to pull request develop ,and delete feature/some_xxx

### 常用调用

XiantaoGameLogicProcessor
1. createVIPTable
2. sendCards
3. notifyNextPlayerOperation
4. player_chu_notify
5. playingTableTick
6. gameTableTick

XiantaoAsyncLogicHandleThread
1. execute_operation_msg
2. playerChuOperation
3. player_chu_notify
4. player_op_peng
5. player_op_gang
6. real_player_op_gang
7. player_op_hu
8. auto_hu_other_player
9. player_hu
10. win_lose_gold_calculate_hu

XiantaoMahjongProcessor
1. check_chi
2. check_peng
3. check_gang_with_hun
4. checkWin
5. doCheckWin

### CardDown 牌组扩展

1. Player    add    CardDown
2. Machine    add    CardDown
3. PlayerService    extends
4. MachineService    etends
5. GameLogicProcessor    	@Qualifier("playerService")    @Qualifier("machineService")
6. AsyncLogicHandleThread    	@Qualifier("playerService")    	@Qualifier("machineService")
7. Player 下 copy_game_state 短线重连恢复数据

### 新手练习

```java
  GameLogicProcessor class
    public void tryEnterRoom(Player pl,int roomID){
      ...
      //GameTable gt=rm.enterCommonTable(pl,SystemConfig.mjType);
      GameTable gt=rm.enterCommonTable(pl,GameConstant.GAME_PLAY_RULE_XIANTAO_YILAIDAODI);
    }
```
SystemConfig.mjType =>  GAME_PLAY_SOME_RULE
SystemConfig.mjType 父类存在默认值

### pom 文件

1. project artifactId
2. manifest mainClass

### 房间金币

1. config_db    global_congif   客户端获取配置信息
2. hubei_heji_xt_db    t_global  服务器通过获取配置信息验证

### 方法名一致性 （1新老框架）（2子类父类）

### 测试打包流程

#### 119.29.222.68 gate测试服务器

1. /data1/home/genghaoliang/RoomProxy_test2/conf
vi proxy.conf

2. /data1/home/genghaoliang/RoomProxy_test2/bin
sa.sh && sp.sh

已迁移至 /data1/games/

#### 115.159.48.163 logic测试服务器

/data1/games/xiantao
1. config
2. lib
3. service.sh
4. xiantao-mahjong-server-1.0.0-SNAPSHOT.jar

### 支付&代理接入

1. server.xml 端口号
2. config.xml
```xml  
  #数据源地址 这里使用 &amp; 其他 使用 &   
  <entry key="dataSource.jdbcUrl">jdbc:mysql://119.29.222.68:3306/hubei_heji_xt_db?useUnicode=true&amp;characterEncoding=utf8</entry>
  <entry key="gameSocketPort">17764</entry>
  <entry key="logicGameId">327941</entry>
```
3. catalina.sh Java 内存限制
