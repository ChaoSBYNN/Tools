package com.szxx.mahjong.xiantao.processor;


import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.sparkingfuture.common.util.CommonUtils;
import com.sparkingfuture.common.util.DateUtil;
import com.sparkingfuture.common.util.UUIDGenerator;
import com.szxx.async.handler.AsyncLogicHandleThread;
import com.szxx.async.handler.XiantaoAsyncLogicHandleThread;
import com.szxx.async.msg.CommonLogicMsg;
import com.szxx.base.networkbase.NetMsgBase;
import com.szxx.base.server.GameSocketServer;
import com.szxx.config.SystemConfig;
import com.szxx.constant.ConstantCode;
import com.szxx.constant.GameConfigConstant;
import com.szxx.constant.HandlerConstant;
import com.szxx.constant.LogConstant;
import com.szxx.domain.*;
import com.szxx.mahjong.xiantao.constant.GameConstant;
import com.szxx.mahjong.xiantao.mahjong.XiantaoMahjongProcessor;
import com.szxx.msg.*;
import com.szxx.processor.logic.GameLogicProcessor;
import com.szxx.processor.mahjong.MahjongProcessor;
import com.szxx.service.IMachineService;
import com.szxx.service.IPlayerService;
import com.szxx.service.ISystemConfigService;
import com.szxx.zuopai.ZuoPaiTool;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * Description:
 * 仙桃逻辑进程
 *
 * @author chris
 */
@SuppressWarnings("ALL")
@Component
public class XiantaoGameLogicProcessor extends GameLogicProcessor{
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(XiantaoGameLogicProcessor.class);

	//CHAOS_ZHANG zuopai file path
	@Value("${zuoPaiFilePath:config/zuopai.txt}")
	private String zuoPaiFilePath;

	protected List<GameRoom> gameRoomList = new ArrayList<GameRoom>();

	@Override
	protected void initThreadList() {
		logicThreadList.clear();
		for (int i = 0; i < logicThreadNum; i++) {
			AsyncLogicHandleThread logicThread = new XiantaoAsyncLogicHandleThread(systemConfigService, machineService,
					mahjongProcessor, playerService);
			logicThread.setThreadIndex(i);
			logicThread.start();
			logicThreadList.add(logicThread);
		}
	}

	@Override
	public void pushOperationMsg(Player pl, PlayerTableOperationMsg msg, GameTable gt, GameRoom gr,
                                 AsyncLogicHandleThread thread)	{
		CommonLogicMsg logicMsg = new CommonLogicMsg();
		logicMsg.player=pl;
		logicMsg.gameTable=gt;
		logicMsg.gameRoom=gr;
		logicMsg.msg=msg;
		logicMsg.handle= HandlerConstant.LOGIC_PLAYER_OPERATION_HANDLE;
		thread.pushMsg(logicMsg);
	}

	//CHAOS_ZHANG xiantao rule begin
	//CHAOS_ZHANG function guide:
	//CHAOS_ZHANG 1.tryEnterRoom 进入房间设置 潜江|仙桃玩法
	//CHAOS_ZHANG 2.sendCards 发牌时决定痞子牌与癞子牌
	//CHAOS_ZHANG 3.gold_calculate_gang_liuju 流局也计算分数
	//CHAOS_ZHANG 4.calLaiziFan 计算癞子番每打出1个癞子最终得分翻倍，依次类推，直至打出4个癞子。
	//CHAOS_ZHANG 					第一个打出癞子的玩家得分为：3x底分x1=3，
	//CHAOS_ZHANG 					第二个打出癞子的玩家得分为：3x底分x2=6，
	//CHAOS_ZHANG 					第三个打出癞子的玩家得分为：3x底分x4=12，
	//CHAOS_ZHANG 					第四个打出癞子的玩家得分为：3x底分x8=24
	//CHAOS_ZHANG 5.chu_end 牌局结束 新老框架字段整合
	//CHAOS_ZHANG 6.game_over_liuju 流局操作 后执行 => gold_calculate_gang_liuju
	//CHAOS_ZHANG 7.notifyNextPlayerOperation 下一个玩家行动提示
	//CHAOS_ZHANG 8.notifyPlayerPlayCard 玩家出牌提示

	//CHAOS_ZHANG 设置游戏规则
	/**创建房间*/
	public void createVIPTable(Player player, CreateVipRoomMsg msg) {
		int vip_rule = msg.selectWayNum;
		GameRoom rm=gameRoomMap.get(msg.roomID);
		if(rm==null)
			return;

		String vip_tb_id = player.getVipTableID();
		if(vip_tb_id!=null && vip_tb_id.length()>1 && rm.getPlayingTablesMap().containsKey(vip_tb_id))
		{
			logger.error("player index:" + player.getPlayerIndex() + ",md5:" + msg.psw + ",ordin vip_rule:"
					+ msg.selectWayNum + ",vip tableid:" + player.getVipTableID());
			return;
		}

		//
		GameTable gt = rm.createVipTable(msg, player);
		if(gt==null)
			return;
		//
		gt.setBaseHandsTotal(msg.quanNum);
		logger.info("tableid="+gt.getTableID()+",hands="+msg.quanNum);

		//vip默认具有的打法，毋须客户端配置
		//lyh有风wufeng
		int default_rule=rm.get_default_vip_table_rule(SystemConfig.mjType);


		//xxx first setting later modify GAME_PLAY_RULE_QIANJIANG_QUANHU => GAME_PLAY_RULE_XIANTAO_YILAIDAODI
		//vip_rule |= GameConstant.GAME_PLAY_RULE_XIANTAO_YILAIDAODI;

		gt.setMaiMaNum(0);

		vip_rule |= default_rule;

		gt.setVipRule(vip_rule);

		//
		Date ct = DateUtil.getCurrentUtilDate();
		gt.setVipCreateTime(ct.getTime());
		//总圈数
		gt.setHandsTotal(msg.quanNum);
		//


		//设置为未支付房卡
		gt.setPayVipKa(false);

		//设置VIP房间带入金额
		player.setHuFanNum(0);
		player.setVipTableGold(0);


		player.setOnTable(true);
		//
		//返回给玩家，
		RequestStartGameMsgAck ack=new RequestStartGameMsgAck();
		ack.setGold(player.getGold());
		ack.setTotalHand(gt.getHandsTotal());
		ack.setCurrentHand(gt.getHandNum());
		ack.setNewPlayWay(gt.getVipRule());
		ack.setResult(ConstantCode.CMD_EXE_OK);
		ack.initPlayers(gt.getPlayers(), gt.isVipTable());
		ack.setRoomID(msg.roomID);
		ack.setTablePos(player.getTablePos());
		ack.setVipTableID(gt.getVipTableID());
		ack.setCreatorName(gt.getCreatorPlayerName());
		ack.setCreatePlayerID(Integer.toString(gt.getCreatorPlayerIndex()));
		//进入桌子,发消息给玩家
		ack.setSessionID(player.getPlayerLobbySessionID());
		GameSocketServer.sendMsg(player.getSession(), ack);

		logger.info("vip table id="+gt.getVipTableID() + " tableID=" + gt.getTableID());

		gt.gameLogic = this;
		player.gameLogic = this;
	}

	/**
	 * 函数：进入房间设置玩法
	 *
	 * @param pl 玩家
	 * @param roomID 房间号
	 */
	public void tryEnterRoom(Player pl,int roomID) {
		//重置异常标志
		pl.setNeedCopyGameState(false);

		//当前正在vip房间中，不能进其他房间,自动进vip房
		if(pl.getVipTableID().length()>0)
		{
			GameRoom rm=gameRoomMap.get(pl.getRoomID());
			if(rm==null)
				return;

			GameTable gt=rm.getTable(pl.getTableID());

			if(gt!=null)
			{
				EnterVipRoomMsg mxg=new EnterVipRoomMsg();
				mxg.setTableID(gt.getTableID());
				mxg.setPsw(gt.getVipPswMd5());
				mxg.setRoomID(pl.getRoomID());
				enterVipRoom(mxg,pl);
				pl.gameLogic = this;
				return;
			}
		}


		GameRoom rm=gameRoomMap.get(roomID);
		//看看是否进老桌子
		if(checkInOldTable(pl))
		{
			pl.gameLogic = this;
			return;
		}
		else
		{
			//看看是否还在桌子上
			List<GameTable> oldTables = this.getPlayingTables(pl.getPlayerIndex());
			if(oldTables != null && oldTables.size() >= 1)
			{
				//还在老桌子中
				GameTable oldTable = oldTables.get(0);
				if(oldTable != null)
				{
					logger.error("玩家:" + pl.getPlayerIndex() + ",断线重连，遍历房间才找到老桌子,tableID=" + oldTable.getTableID());

					pl.setRoomID(oldTable.getRoomID());
					pl.setTableID(oldTable.getTableID());
					pl.setNeedCopyGameState(true);	//玩家数据异常需要恢复游戏数据

					if(oldTable.isVipTable()){
						pl.setVipTableID(oldTable.getTableID());

						//VIP房间处理
						EnterVipRoomMsg mxg=new EnterVipRoomMsg();
						mxg.setTableID(oldTable.getTableID());
						mxg.setPsw(oldTable.getVipPswMd5());
						mxg.setRoomID(pl.getRoomID());
						enterVipRoom(mxg,pl);
						pl.gameLogic = this;
						return;
					} else {
						if(checkInOldTable(pl))
						{
							pl.gameLogic = this;
							return;
						}
					}
				}
			}


			//校验金币是否足够
			if(!validatePlayerGold(rm,pl))
				return;
		}

		//
		GameTable gt=rm.enterCommonTable(pl,SystemConfig.mjType);
		gt.gameLogic = this;
		pl.gameLogic = this;

		//CHAOS_ZHANG first setting qianjiang quan_hu later to xiantao some rule
		//设置玩法
		//CHAOS_ZHANG GAME_PLAY_RULE_QIANJIANG_QUANHU => GAME_PLAY_RULE_XIANTAO_GANDENGYAN

		// 设置剩余码数
		gt.setMaiMaNum(0);

		enterTableDone(pl,gt,false);
		machineService.newset_machinetime(pl,gt);
	}

	/**
	 * 函数：发牌
	 *
	 * @param gt 牌局
	 * @param service_gold 台费
	 * @param operationTime 操作时间 uncertainty
	 * @param isDealerAgain 是否连庄
	 */
	public void sendCards(GameTable gt,int service_gold, int operationTime, int isDealerAgain) {
		//当前轮到庄家打牌
		gt.sendCards();
		int dealerPos=gt.getDealerPos();

		List<Player> players= gt.getPlayers();
		List<Byte> cards=gt.getCards();

		//先清理
		for(int i=0;i<players.size();i++)
		{
			Player pl=players.get(i);
			pl.clearCards();
		}

        byte riffraffCard = 0x0;
        byte lazarailloCard = 0x0;

		//CHAOS_ZHANG 硬晃没有癞子
        boolean isYingHuang = (gt.getVipRule()&GameConstant.GAME_PLAY_RULE_XIANTAO_YINGHUANG)
                ==GameConstant.GAME_PLAY_RULE_XIANTAO_YINGHUANG;

		if(!isYingHuang){

            //CHAOS_ZHANG insert qianjiang rule card
            //CHAOS_ZHANG setting last card is laizi , setting and remove
            riffraffCard = gt.popLastCard(1);

            int color = riffraffCard & 0xf0;
            int value = riffraffCard & 0x0f;

            //CHAOS_ZHANG 潜江、仙桃 分配癞子方法 仅适用不存在 字牌、花牌
            value = (value + 1) % 10;
            if (value == 0) {
                value = 0x01;
            }

            lazarailloCard = (byte)(color | value);

            //CHAOS_ZHANG set pizi card and laizi card
            gt.setRiffraffCard(riffraffCard);
            gt.setLazarailloCard(lazarailloCard);

        }

		for(int i=0;i<players.size();i++)
		{
			Player pl=players.get(i);
			//
			List<Byte> cl=pl.getCardsInHand();

			cl.clear();

			List<Byte> src=new ArrayList<Byte>();



            for(int j=0;j<13;j++) {
                Byte b=cards.remove(0);
                src.add(b);
            }

			//庄家开始多抓一张
			if(i==dealerPos) {
				Byte b=cards.remove(0);
//				src.add(b);
				pl.setCardGrab(b);
				//
				pl.setMoCardNum(1);//庄家默认已经摸了一张

				gt.setLastMoCardPlayerTablePos(pl.getTablePos());
			}
			//排个序
			gt.sortCards(src,cl);
			src.clear();
			//把牌发给玩家
			GameStartMsg msg=new GameStartMsg();


			for(Player plx :gt.getPlayers()){
				logger.info( plx.getPlayerName() + "   " + plx.getHuFanNum() + " " + plx.getVipTableGold());
			}


			msg.newPlayWay=gt.getVipRule();
			msg.myCards=cl;
			msg.myTablePos=pl.getTablePos();
			msg.quanNum=gt.getHandNum();//gt.getQuanNum();
			msg.dealerPos=dealerPos;

			//chaos_zhang save pizi laizi to msg
            if(!isYingHuang){
                msg.baoCard=riffraffCard|(lazarailloCard<<8);
            }
			msg.setup_gold(players,gt.isVipTable());
			msg.serviceGold = gt.getHandsTotal();
			msg.playerOperationTime = operationTime;
			msg.isDealerAgain = isDealerAgain;
			msg.sessionID=pl.getPlayerLobbySessionID();

			SystemConfig.gameSocketServer.sendMsgWithSeq(pl.getSession(), msg, pl.getPlayerID(), gt);
		}
	}

	/**
	 * 函数：潜江发牌规则
	 * 只有筒、条、万共108张牌
	 *
	 * @param gt 牌局
	 */
	public void washCards(GameTable gt) {
		int handNum=gt.getHandNum();
		gt.setHandNum(++handNum);
		logger.info("-------->开始洗牌:tableID="+gt.getTableID()+",handNum="+handNum);

		gt.setLouBaoCheckPlayer(null);
		gt.setDuoXiangPaoPl(null);
		gt.setPlayerShowXiaJingCard(false);
		gt.setChuPlayer(null);
		gt.setLazarailloCard((byte) 0);

		gt.setWaitingTianhuOrBuHua(false);

		gt.clearCloseVipRoomMap();

		for (Player player : gt.getPlayers()) {
			player.clearHua();
			player.setTableID(gt.getTableID());
		}

		List<Byte> cards = gt.getCards();

		gt.getCards().clear();
		gt.getTingPlayerIDList().clear();
		gt.setGangPlayer(null);
		//CHAOS_ZHANG GameTable add @Data
		gt.getDownCards().clear();


		//默认有万条筒
		for(int j=0;j<3;j++) {
			for(int i=1;i<=9;i++)
			{
				int ib=(j<<GameConstant.MAHJONG_CODE_COLOR_SHIFTS)+i;
				byte b=(byte)(ib&0xff);
				cards.add(b);
				cards.add(b);
				cards.add(b);
				cards.add(b);
			}
		}


		long seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
		seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
	}

	/**
	 * 函数：流局后计算分数
	 *
	 * @param gt 牌局
	 */
	public void gold_calculate_gang_liuju(GameTable gt) {
		logger.info("开始计算流局分数");
		byte laizi = gt.getLazarailloCard();
		int dizhu = gt.getBaseGold();
		//记录每个玩家癞子杠的倍数
		List<Player> plist = gt.getPlayers();
		for (Player player : plist) {
			player.setHuDesc("");
			player.setWinLoseGoldNum(0);
			int laiziNum = 0;
			List<CardDown> allCardDown = player.getCardsDown();
			for (CardDown cardDown : allCardDown) {
				if (((byte) cardDown.cardValue)==laizi) {
					laiziNum++;
				}
			}
			int laiziFan = calLaiziFan(laiziNum);
			if (laiziFan > 1) {
				player.setHuDesc("癞子杠*"+laiziFan+":");
				logger.info(player.getPlayerIndex()+":玩家癞子杠次数"+laiziNum+",癞子杠翻数:"+laiziFan);

			}
			player.setBao2Score(1);

		}

		//CHAOS_ZHANG 飘癞子奖励分 开始
		HashMap<String,String> laiziMap = new HashMap<String,String>();
		int cardDownLaizi = gt.getVisibleCardNum(gt.getLazarailloCard());
		//CHAOS_ZHANG 获取 癞子 并且 按照癞子键值对 put 进入 map
		for(int i = 1;i<=cardDownLaizi;i++){
			for(Player player : plist){
				List<Byte> laiziCardDown =((XiantaoPlayer)player).getCardsDownLaizi();
				for(Byte card : laiziCardDown){
					if(i==card){
						laiziMap.put(i+"",((XiantaoPlayer)player).getPlayerID());
					}
				}
			}
		}

		for(int i = 1;i<=4;i++){
			String laiziPlayer = laiziMap.get((i+""));
			if(null == laiziPlayer){
				break;
			}
			//CHAOS_ZHANG 当前癞子底数 查找同一玩家打出该癞子前癞子的次数
			int base = getBase(i,laiziMap,laiziPlayer);
			//CHAOS_ZHANG 当前张癞子打出奖励 底番 (默认一次癞子 base = 0)
			int laiziScore = 1 << base;
			//CHAOS_ZHANG 计某个玩家当前癞子打出后总奖励
			int count_win = 0;

			for (Player player : plist) {
				//CHAOS_ZHANG 飘癞子3赔1 癞子玩家不赔
				if(laiziPlayer.equals(player.getPlayerID())){
					continue;
				}
				//CHAOS_ZHANG 玩家总扣分统计
				int laizi_lose = 1;
				//CHAOS_ZHANG 当前癞子底数 其他玩家小于该癞子
				int obase = getBase(i,laiziMap,player.getPlayerID());

				//CHAOS_ZHANG 如果其他玩家也打出过癞子 计算癞子翻倍 赔偿 (打出癞子顺序小于该顺序)
				boolean b1 = laiziMap.containsValue(player.getPlayerID());
				boolean b2 = obase>0;

				if(b1&&b2){
					laizi_lose = laiziScore * (1 << obase);
				}else{
					laizi_lose = laiziScore;//CHAOS_ZHANG 正常算分
				}

				count_win+=laizi_lose;//CHAOS_ZHANG 得分累加

				player.setWinLoseGoldNum(player.getWinLoseGoldNum() + (0 - (laizi_lose)));//CHAOS_ZHANG 他人飘癞子总扣分
				player.setHuDesc(player.getHuDesc()+"飘癞子-" + laizi_lose);

				logger.info("****** CHAOS_ZHANG ****** player{} 飘{}癞子: {}",player.getPlayerIndex(),base,(0 - (laizi_lose)));

			}

			for(Player lzplayer : plist){//CHAOS_ZHANG 最后算飘癞子 总奖励 查找当前飘癞子玩家
				if(laiziPlayer.equals(lzplayer.getPlayerID())){
					lzplayer.setWinLoseGoldNum(lzplayer.getWinLoseGoldNum() + count_win);
					lzplayer.setHuDesc(lzplayer.getHuDesc()+"飘癞子+" + count_win);

					logger.info("****** CHAOS_ZHANG ****** lzplayer{} 飘{}癞子: {}",lzplayer.getPlayerIndex(),base,count_win);
					break;
				}
			}
		}
		//CHAOS_ZHANG 癞子奖励分 结束

		//CHAOS_ZHANG
		//CHAOS_ZHANG 最终癞子番数 胡计分 使用
		int baseLaiziFan = calLaiziFan(gt.getVisibleCardNum(gt.getLazarailloCard()));

		//CHAOS_ZHANG 手上没有癞子，没有打过癞子，才能胡热铳

		//计算补杠、明杠牌的分数
		byte pizi = gt.getRiffraffCard();
		logger.info("开始计算杠牌分数");
		for (Player player : plist) {
			int i=0;
			//CHAOS_ZHANG Player to XiantaoPlayer
			for (XiantaoCardDown cd : ((XiantaoPlayer)player).getCardsDownXiantao()) {

				i++;
				int size = player.getCardsDown().size();//玩家出牌列表，不含癞子杠


				if(cd.type == GameConstant.MAHJONG_OPERTAION_BU_GANG){
					//CHAOS_ZHANG 得分技术
					int total_gang_win = 0;

					//CHAOS_ZHANG 当前杠 癞子个数 及 番数
					//FIXME 飘癞子 杠算分
					int laiziFan = calLaiziFan(cd.lazarailloNum);

					//CHAOS_ZHANG 补杠 底分1倍 三家赔 3*1*底分=3
					int gangScore = 1 * laiziFan;

					for (Player gang_player : plist) {
						int gang_lose = 0;
						if (player.getPlayerID().equals(gang_player.getPlayerID())) {
							continue;
						}
						//CHAOS_ZHANG 单人杠分数
						gang_lose = player.getBao2Score() * gang_player.getBao2Score() * gangScore;
						//CHAOS_ZHANG 杠家计总分
						total_gang_win += gang_lose;
						//CHAOS_ZHANG 其他人扣分 其他规则 + 杠扣掉的分数
						gang_player.setWinLoseGoldNum(gang_player.getWinLoseGoldNum()+(0-gang_lose));
						//CHAOS_ZHANG 杠后描述
						gang_player.setHuDesc(gang_player.getHuDesc()+" 回头笑-"+gang_lose);

						logger.info(player.getPlayerIndex()+":玩家补杠牌"+cd.cardValue);
						logger.info(player.getPlayerIndex()+":玩家补杠扣玩家:"+gang_player.getPlayerIndex()+":"+player.getPlayerIndex()+"癞子翻"+player.getBao2Score() +"*"+gang_player.getPlayerIndex()+"癞子翻"+gang_player.getBao2Score() +"* 1="+gang_lose);
					}
					//CHAOS_ZHANG 杠家计总分
					player.setWinLoseGoldNum(player.getWinLoseGoldNum()+total_gang_win);
					//CHAOS_ZHANG 杠家描述
					player.setHuDesc(player.getHuDesc()+" 回头笑+"+total_gang_win);

					logger.info(player.getPlayerIndex()+":玩家补杠加分:"+total_gang_win);

					//CHAOS_ZHANG 暗杠  不确定 GameConstant.MAHJONG_OPERTAION_LIANG_PI_ZI 黑笑朝天与黑笑 分数相同 不单算
				} else if (cd.type == GameConstant.MAHJONG_OPERTAION_AN_GANG||(cd.type == GameConstant.MAHJONG_OPERTAION_LIANG_PI_ZI)) {
					//CHAOS_ZHANG 暗杠计分
					int total_gang_win = 0;

					//CHAOS_ZHANG 当前杠 癞子个数 及 番数
					//FIXME 飘癞子 杠算分
					int laiziFan = calLaiziFan(cd.lazarailloNum);

					//CHAOS_ZHANG 暗杠 底分2倍 三家赔 3*2*底分=6
					int gangScore = 2 * laiziFan;

					for (Player gang_player : plist) {

						int gang_lose = 0;
						if (player.getPlayerID().equals(gang_player.getPlayerID())) {
							continue;
						}
						//CHAOS_ZHANG 单人杠分数
						gang_lose = player.getBao2Score() * gang_player.getBao2Score() * gangScore;
						//CHAOS_ZHANG 杠家计总分
						total_gang_win += gang_lose;
						//CHAOS_ZHANG 其他人扣分 其他规则 + 杠扣掉的分数
						gang_player.setWinLoseGoldNum(gang_player.getWinLoseGoldNum()+(0-gang_lose));
						//CHAOS_ZHANG 杠后描述
						gang_player.setHuDesc(gang_player.getHuDesc()+" 闷笑-"+gang_lose);

						logger.info(player.getPlayerIndex()+":玩家暗杠牌"+cd.cardValue);
						logger.info(player.getPlayerIndex()+":玩家暗杠扣玩家:"+gang_player.getPlayerIndex()+":"+player.getPlayerIndex()+"癞子翻"+player.getBao2Score() +"*"+gang_player.getPlayerIndex()+"癞子翻"+gang_player.getBao2Score() +"* 2="+gang_lose);
					}
					//CHAOS_ZHANG 计分
					player.setWinLoseGoldNum(player.getWinLoseGoldNum()+total_gang_win);
					//CHAOS_ZHANG 描述
					player.setHuDesc(player.getHuDesc()+" 闷笑+"+total_gang_win);

					logger.info(player.getPlayerIndex()+":玩家暗杠加分:"+total_gang_win);

					//CHAOS_ZHANG 明杠 （碰痞子 => 明杠） 点笑朝天与点笑分数相同 不单算
				}else if (cd.type == GameConstant.MAHJONG_OPERTAION_MING_GANG || (cd.type == GameConstant.MAHJONG_OPERTAION_PENG && pizi == (byte) cd.cardValue)) {

					//CHAOS_ZHANG 当前杠 癞子个数 及 番数
					//FIXME 飘癞子 杠算分
					int laiziFan = calLaiziFan(cd.lazarailloNum);

					//CHAOS_ZHANG 明杠 底分1倍 一家赔 1*1*底分=1
					int gangScore = 1 * laiziFan;

					int gang_lose = player.getBao2Score() * cd.chuPlayer.getBao2Score() * gangScore;

					//CHAOS_ZHANG 出牌玩家 扣分 与 描述
					cd.chuPlayer.setWinLoseGoldNum(cd.chuPlayer.getWinLoseGoldNum() + (0 - gang_lose));
					cd.chuPlayer.setHuDesc(cd.chuPlayer.getHuDesc() + " 点笑-" + gang_lose);

					//CHAOS_ZHANG 杠玩家 得分 与 描述
					player.setWinLoseGoldNum(player.getWinLoseGoldNum() + gang_lose);
					player.setHuDesc(player.getHuDesc() + " 点笑+" + gang_lose);

					logger.info(player.getPlayerIndex()+":玩家明杠牌"+cd.cardValue);
					logger.info(player.getPlayerIndex()+":玩家暗杠扣玩家:"+cd.chuPlayer.getPlayerIndex()+":"+player.getPlayerIndex()+"癞子翻"+player.getBao2Score() +"*"+cd.chuPlayer.getPlayerIndex()+"癞子翻"+cd.chuPlayer.getBao2Score() +"* 2="+gang_lose);
					logger.info(player.getPlayerIndex()+":玩家暗杠加分:"+gang_lose);
				}
			}
		}

		for (Player player : plist) {
			logger.info(player.getPlayerIndex()+"玩家本局得分:"+player.getWinLoseGoldNum()*dizhu);
			player.setWinLoseGoldNum(player.getWinLoseGoldNum()*dizhu);
			player.setBao2Score(0);
		}
	}

	/**
	 * CHAOS_ZHANG 癞子次数 ： 在某一次飘癞子之前该玩家 飘癞子次数
	 *
	 * @param laiziNum 当前癞子次数
	 * @param laiziMap 癞子Map
	 * @param playerId 某一玩家
	 * @return baseModulus  当前癞子底数(模数)
	 */
	public int getBase(int laiziNum,HashMap<String,String> laiziMap,String playerId){
		//CHAOS_ZHANG 当前癞子底数(模数)
		int baseModulus = 0;
		//CHAOS_ZHANG lessLaiziNum ： 小于laiziNum时循环查找
		for(int lessLaiziNum=1;lessLaiziNum<laiziNum;lessLaiziNum++){
			//CHAOS_ZHANG 在此飘癞子之前同一玩家 飘过癞子次数
			if(playerId.equals(laiziMap.get((lessLaiziNum+"")))){
				baseModulus++;
			}
		}
		return baseModulus;
	}

	/**
	 * 函数：计算癞子番数
	 *
	 * @param laiziNum 癞子数
	 * @return
	 */
	private int calLaiziFan(int laiziNum) {
		int laizifan = 1;
		for (int i = 0; i < laiziNum; i++) {
			laizifan = laizifan * 2;
		}
		return laizifan;
	}

	/**
	 * 函数：出牌结束，没有吃碰胡
	 *
	 * @param gt 牌局
	 */
	public void chu_end(GameTable gt) {
		byte card=gt.getCurrentCard();
		if(card==0)
			return;//没有出的牌

		int idx=gt.getCardOpPlayerIndex();
		Player pl=gt.getPlayerAtIndex(idx);
		pl.addCardBefore(card);
		logger.info("chu_end=" + Integer.toHexString(card));
		//
		//现在客户端自动放在桌子一张牌，不需要这个了，//如果被碰走，服务器会通知客户端拿走那张被碰的牌
		//添加一张出的牌，在某个玩家的面前，没有被吃碰胡
		PlayerOperationNotifyMsg chuMsg=new PlayerOperationNotifyMsg();
		chuMsg.operation=GameConstant.MAHJONG_OPERTAION_ADD_CHU_CARD;
		//chaos_zhang 	chuMsg.player_table_pos=pl.getTablePos(); => chuMsg.setPlayerTablePos(pl.getTablePos());
		chuMsg.setPlayerTablePos(pl.getTablePos());
		chuMsg.target_card=card&0xff;
		chuMsg.cardLeftNum=gt.getCardLeftNum();
		chuMsg.sessionID=pl.getPlayerLobbySessionID();
		this.sendMsgToTable(gt,chuMsg,1);

		//
		gt.setCurrentCard((byte)0);
		gt.setCardOpPlayerIndex(-1);
		//
	}

	/**
	 * 函数：流局游戏结束
	 *
	 * @param gt 牌局
	 */
	public void game_over_liuju(GameTable gt) {
		//流局不流局，杠必须算分
		gold_calculate_gang_liuju(gt);
		gt.setLiuJuNoPlayerHu(true);

		//合并计算提交数据库
		win_lose_money_submit(gt);
		//
		gt.setState(GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN);

		//chaos_zhang DateService.getCurrentUtilDate().getTime(); => DateUtil.getCurrentUtilDate().getTime();
		long ctt = DateUtil.getCurrentUtilDate().getTime();
		gt.setHandEndTime(ctt);
	}

	/**
	 * 函数：有玩家打了一张牌，现在看看下一个玩家怎么行动，可能是胡，吃，碰
	 *
	 * @param gt 牌局
	 */
	private void notifyNextPlayerOperation(GameTable gt) {
		byte card=gt.getCurrentCard();
		Player chuPlayer=gt.getPlayerAtIndex(gt.getCardOpPlayerIndex());

		//chaos_zhang insert qiangjiang rule
		byte riffraffCard = gt.getRiffraffCard();
		byte lazarailloCard = gt.getLazarailloCard();

		Player pl = gt.getCurrentOperationPlayer();
		if (lazarailloCard == card) {
			byte tailCd = gt.popLastCard(1);
			//tailCd = (byte)0x26;
			pl.setCardGrab(tailCd);

			//CHAOS_ZHANG 玩家出牌 癞子操作
			int laiziNum = gt.getVisibleCardNum(gt.getLazarailloCard());
			((XiantaoPlayer)pl).setCardsDownLaizi((byte)(laiziNum));


			if(card == lazarailloCard) {
				//如果桌上没牌了，出癞子流局
				if(gt.isEnd())//流局了
				{
					game_over_liuju(gt);
					return;
				}

				pl.addCardDown(lazarailloCard, lazarailloCard, lazarailloCard, GameConstant.MAHJONG_OPERTAION_LAIZI_GANG);
				MahjongCheckResult waiting = new MahjongCheckResult();
				waiting.playerTableIndex = chuPlayer.getTablePos();
				waiting.targetCard = chuPlayer.getCardGrab();
				waiting.fanResult |= GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;
				gt.setWaitingPlayerOperate(waiting);
				chu_end(gt);

				notifyPlayerPlayCard(gt,true,true);//杠完能胡
				return;

			}
		}


		//
		//顺序，轮到下一个玩家行动
		gt.moveToNextPlayer();
		//
		Player plx=gt.getCurrentOperationPlayer();
		if(plx==null)
			return;
		//

		//

		if(gt.isEnd())//流局了
		{
			//最后一张打出的不知道是否能胡       先能胡吧！！lyh
			checkNextPlayerOperation(gt,card,chuPlayer,GameConstant.MAHJONG_OPERTAION_HU);
			//最后一张牌如果打出来每人胡就是流局   lyh
			//如果每人胡会把金币提交两次，所以加一个判断lyh
			if(gt.getWaitingPlayerOperate()==null && !(gt.isGoldChangeCommited()))
				game_over_hu(gt,true);
			return;
		}
		else
		{
			//可以吃，碰，杠， 点炮
			checkNextPlayerOperation(gt,card,chuPlayer,
					//chaos_zhang not have chi operation
					//GameConstant.MAHJONG_OPERTAION_CHI
					GameConstant.MAHJONG_OPERTAION_PENG
							|GameConstant.MAHJONG_OPERTAION_MING_GANG
							|GameConstant.MAHJONG_OPERTAION_HU);
		}
	}

	private void playingTableTick(GameTable gt, long ctt)
	{
		int substate=gt.getPlaySubstate();
		Player currentPl=null;
		//
		int ext_wait_time=0;


		MahjongCheckResult  waiting=gt.getWaitingPlayerOperate();
		if(waiting!=null)//如果有等待操作，就让对方操作
			currentPl=gt.getPlayerAtIndex(waiting.playerTableIndex);
		else//如果没有等待玩家，就是当前按位置的玩家
			currentPl=gt.getCurrentOperationPlayer();

		if(currentPl==null)
		{
			gt.addTickError();
			//错误大于300次自动清除桌子
			if(gt.getTickError()>3000){
				//清除桌子
				//先测试金币够不够
				for(int i=0;i<gt.getPlayers().size();i++)
				{
					Player pl=gt.getPlayers().get(i);
					//player_left_table(pl);
					logger.error("current operation player not found contail player:"+pl.getPlayerIndex());
				}


				logger.error("current operation player not found, tableID=" + gt.getTableID()
						+ ",State=" + gt.getState() + ",BackUpState=" + gt.getBackupState()
						+ ",GameStartTime=" + gt.getGameStartTime()
						+ ",CurrentOperateIndex=" + gt.getCurrentOpertaionPlayerIndex());

				gt.clearGameState();
				gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);
				gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
				gt.setHandEndTime(System.currentTimeMillis());

				//设为非法状态，系统会自动清理
				gt.setState(GameConstant.TABLE_STATE_INVALID);

				gt.reSetTickError();
			}

			return;
		}


		if(substate==GameConstant.GAME_TABLE_SUB_STATE_PLAYING_CHI_PENG_ANIMATION)
		{
			if(ctt-gt.getWaitingStartTime()>1000+ext_wait_time)//动画播完
			{
				gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_IDLE);
				//吃完，碰完不能立刻胡
				boolean could_hu=false;
				//通知玩家出牌
				player_chu_notify(gt, false, true);
			}
		}
		else if(substate==GameConstant.GAME_TABLE_SUB_STATE_PLAYING_GANG_ANIMATION){
			if(ctt-gt.getWaitingStartTime()>1000+ext_wait_time)//动画播完
			{
				gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_IDLE);
				//吃完，碰完不能立刻胡
				boolean couldHu = true;//杠完可以胡，杠开

				//判断2个底牌是否杠开，如果可以刚开，上花

				player_chu_notify(gt,couldHu,true);
			}
		}
		else if(substate==GameConstant.GAME_TABLE_SUB_STATE_PLAYING_CHU_ANIMATION)
		{

			if(ctt-gt.getWaitingStartTime()>500+ext_wait_time)//动画播完
			{
				//下一个玩家操作
				gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_IDLE);
				//找找下个玩家
				notifyNextPlayerOperation(gt);
			}
		}else if (substate == GameConstant.GAME_TABLE_SUB_STATE_PLAYING_HU_ANIMATION) {
			//CHAOS_ZHANG 潜江与德宏不同
			if (ctt - gt.getWaitingStartTime() > 500 + ext_wait_time)//动画播完
			{
				//下一个玩家操作
				gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_IDLE);
				//查找下一个可以胡的玩家
				next_player_hu_notify(gt);
			}
		}
		else if(substate==GameConstant.GAME_TABLE_SUB_STATE_IDLE)
		{
			//普通房间，如果玩家时托管状态，则直接帮他操作，否则等他超时
			if( (gt.isVipTable()==false) && ((1 == currentPl.getAutoOperation()) || (ctt-currentPl.getOpStartTime()>(systemConfigService.getPara(GameConfigConstant.CONF_OPERATION_OVER_TIME).getValueInt()*1000+2000))) )//玩家超时
			{
				//防止超时时候，客户端进行出牌操作
				//currentPl.setOverTimeState(GameConstant.PALYER_GAME_OVERTIME_STATE_IN_TABLE_WAITING_TO_OVERTIMECHU);
				//
				//int idx=gt.getCurrentOpertaionPlayerIndex();
				//if(currentPl.getTablePos() != idx){
				//return;
				//}

				processOverTime(currentPl,gt);
			}
		}

	}

	/**
	 * 函数：玩家出牌
	 *
	 * @param gt 牌局
	 * @param couldHu 能胡
	 * @param couldGang 能杠
	 */
	protected void player_chu_notify(GameTable gt,boolean couldHu,boolean couldGang) {
		Player plx=gt.getCurrentOperationPlayer();
		if(plx==null)
			return;

		logger.info("notifyPlayerPlayCard, playerName=" + plx.getPlayerName() + " tableID=" + plx.getTableID());
		//
		byte newCard=plx.getCardGrab();

		PlayerOperationNotifyMsg msg=new PlayerOperationNotifyMsg();

		MahjongCheckResult wait=new MahjongCheckResult();
		wait.playerTableIndex=plx.getTablePos();
		wait.opertaion=0;
		wait.targetCard=newCard;

		//
		MahjongCheckResult hu_xx =null;
		//
		boolean player_could_hu=false;

		if(plx.could_shifeng(newCard))
		{
			hu_xx = new MahjongCheckResult();
			hu_xx.opertaion =  GameConstant.MAHJONG_HU_CODE_SHIFENG;
			hu_xx.playerTableIndex = plx.getTablePos();
			hu_xx.targetCard = newCard;
			wait.targetCard=newCard;

			player_could_hu = true;
		}


		if(couldHu && hu_xx == null)//例如玩家碰完不能直接胡，必须出一张；例如玩家手里剩4张2对，别人出牌他不胡，碰，碰完也不能胡，必须打出一张
		{

			hu_xx = mahjongProcessor.checkWin(newCard, plx,gt);
			//
			if(hu_xx!=null)//胡了
			{
				//if(plx.getMoCardNum()==1)
				//hu_xx.fanResult |=GameConstant.MAHJONG_HU_CODE_DI_HU;
				player_could_hu=true;
			}

		}

		//提示玩家出牌
		msg.operation |=GameConstant.MAHJONG_OPERTAION_CHU;
		msg.setPlayerTablePos(plx.getTablePos());
		msg.setChiCardValue(newCard);
		msg.cardLeftNum=gt.getCardLeftNum();
		msg.setChiFlag(0);
		//

		//自己摸，看看各种杠
		MahjongCheckResult gang_result=null;
		if(couldGang)
		{
			gang_result=mahjongProcessor.checkGang(gt,newCard,plx,true);
		}

		if(gang_result!=null)
		{
			byte hu_card = 0x0;


			//提示玩家听牌，也可以出牌

			msg.gangList = gang_result.gangList;
			msg.setPengCardValue(gang_result.getPengCardValue());
			msg.operation |=gang_result.opertaion;
			//
			wait.setPengCardValue(gang_result.getPengCardValue());
			wait.setChiCardValue(hu_card);
			wait.gangList = gang_result.gangList;
			logger.info("waitting gang size:"+wait.gangList.size());

		}

		if(player_could_hu)
		{
			msg.operation |=GameConstant.MAHJONG_OPERTAION_HU;
			if( (hu_xx.fanResult&GameConstant.MAHJONG_HU_CODE_HEIMO) !=0) {
				wait.fanResult |= GameConstant.MAHJONG_HU_CODE_HEIMO;
			}
		}

		//

		//
		wait.opertaion=msg.operation;

		int waitFan = getWaitFan(gt);
		wait.addFanResult(waitFan);
		if ((wait.fanResult & GameConstant.MAHJONG_HU_CODE_FANG_GANG_HU) != 0) {
			wait.chuPlayer = gt.getWaitingPlayerOperate().chuPlayer;
		}

		gt.setWaitingPlayerOperate(wait);
		//
		long ctt=DateUtil.getCurrentUtilDate().getTime();
		plx.setOpStartTime(ctt);

		//
		//发给其他玩家，让他们知道当前轮到谁操作
		PlayerOperationNotifyMsg msg2=new PlayerOperationNotifyMsg();
		msg2.operation=GameConstant.MAHJONG_OPERTAION_TIP;
		msg2.cardLeftNum=gt.getCardLeftNum();
		msg2.setPlayerTablePos(plx.getTablePos());
		//msg2.use_xiaoji=msg.use_xiaoji;
		this.sendMsgToTable(gt,msg2,1);

		//
		if(plx.isRobot()){
			machineService.postMsg(gt, plx, msg);
		}

		//发消息给玩家，提示他出牌
		msg.sessionID=plx.getPlayerLobbySessionID();
		SystemConfig.gameSocketServer.sendMsg(plx.getSession(), msg);
	}

	protected void next_player_hu_notify(GameTable gt) {
		byte card = gt.getCurrentCard();
		Player chuPlayer = gt.getPlayerAtIndex(gt.getCardOpPlayerIndex());
		//顺序，轮到下一个玩家行动
		gt.moveToNextPlayer();
		//
		Player plx = gt.getCurrentOperationPlayer();
		if (plx == null)
			return;

		check_next_player_hu(gt, card, chuPlayer);
	}

	public void check_next_player_hu(GameTable gt, byte card, Player opPlayer) {
		if (opPlayer == null)
			return;

		//
		MahjongCheckResult result = null;
		PlayerOperationNotifyMsg msg = new PlayerOperationNotifyMsg();

		result = table_loop_check(gt, card, opPlayer, GameConstant.MAHJONG_OPERTAION_HU, opPlayer.getTablePos());
		if (result != null) {
			logger.info("可以胡牌了");
			int waitFan = getWaitFan(gt);
			result.addFanResult(waitFan);
			result.chuPlayer = opPlayer;
			//
			msg.operation = GameConstant.MAHJONG_OPERTAION_HU;
			msg.target_card = result.targetCard;

			//设置当前桌子的当地玩家操作，等玩家操作的时候，再核查一下
			result.opertaion = msg.operation;//以msg的操作为准；
			//
			gt.setWaitingPlayerOperate(result);
			//
			msg.setChiCardValue(result.getChiCardValue());
			msg.setPengCardValue(result.getPengCardValue());
			msg.setPlayerTablePos(result.getPlayerTableIndex());
			msg.target_card = result.targetCard;
			msg.cardLeftNum = opPlayer.getTablePos();        //吃、碰、听谁的,借用这个字段
			//
			//
			Player pl = gt.getPlayerAtIndex(msg.getPlayerTablePos());

			//设置操作开始时间
			long ctt = DateUtil.getCurrentUtilDate().getTime();
			pl.setOpStartTime(ctt);


			//其他从操作发给自己
			msg.sessionID = pl.getPlayerLobbySessionID();
			//SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msg);
			this.sengMsgToPlayer(gt, pl, msg);
		}else {
			game_over_hu(gt, false, opPlayer);
		}

	}

	public void game_over_hu(GameTable gt, boolean isLiuJu, Player paoPlayer) {
		//不是流局
		if (isLiuJu == false) {
			//记录胡的玩家
			List<Player> huPlayers = new ArrayList<Player>();
			List<Player> players = gt.getPlayers();
			for (Player player : players) {
				if (player.isWin()) {
					huPlayers.add(player);
				}
			}
			//如果一炮多响，庄家就是放炮的人
			if (huPlayers.size() > 1 ) {
				logger.info("一炮多响," + paoPlayer.getPlayerName() + "玩家放炮");
				gt.setCurrentHuPlayerIndex(paoPlayer.getTablePos());
			}

			gt.setLiuJuNoPlayerHu(false);
			//
			game_over_update_vip(gt);

		} else {
			gt.setLiuJuNoPlayerHu(true);
		}
		//合并计算提交数据库
		win_lose_money_submit(gt);
		//
		gt.setState(GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN);

		//
		long ctt = DateUtil.getCurrentUtilDate().getTime();
		gt.setHandEndTime(ctt);

	}

	//CHAOS_ZHANG 异步线程
	public void playerOperation(PlayerTableOperationMsg msg,Player pl) {
		if(msg==null || pl==null)
			return;

		//查找vip房间
		if(msg.operation==GameConstant.MAHJONG_OPERTAION_SEARCH_VIP_ROOM)
		{
			search_vip_room(msg,pl);
			return;
		}

		//
		GameRoom gr=this.getRoom(pl.getRoomID());
		if(gr==null)
			return;


		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null){
			return;
		}

		if(this.checkMsgSeq(msg, pl, gt)){
			logger.info("checkMsgSeq return, sseq:"+msg.serverSeq+",cseq:"+msg.clientSeq+",uid:"+msg.uid+",cmd:"+msg.msgCMD);
			return;
		}


		//游戏结束，继续，开始
		if(msg.operation==GameConstant.MAHJONG_OPERTAION_GAME_OVER_CONTINUE)
		{
			game_continue(pl);
			return;
		}


		//玩家主动续卡
		if(msg.operation==GameConstant.MAHJONG_OPERTAION_EXTEND_CARD_REMIND)
		{
			extend_vip_table(msg,pl);
			return;
		}

		//有玩家同意关闭房间
		if(msg.operation==GameConstant.MAHJONG_OPERTAION_WAITING_OR_CLOSE_VIP)
		{
			close_vip_table(msg,pl);
			return;
		}

		//获取关闭房间全量
		if(msg.operation==GameConstant.MAHJONG_OPERTAION_GET_CLOSE_VIP_ROOM_MSG){
			this.sendCloseVipTableAck(gt,false,pl);
			return;
		}

		//剩余多少张牌
		msg.cardLeftNum=gt.getCardLeftNum();
		////玩家出牌
		if((msg.operation&GameConstant.MAHJONG_OPERTAION_CHU)==GameConstant.MAHJONG_OPERTAION_CHU)
		{
			if(pl.getOverTimeState()==GameConstant.PALYER_GAME_OVERTIME_STATE_IN_TABLE_WAITING_TO_OVERTIMECHU)
			{
				return;
			}
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}

			logger.info("当前玩家="+pl.getPlayerName()+" player_op_chu"+",tableID="+pl.getTableID());
			//uncertainty
           /* player_op_chu(gr,gt,msg,pl);*/

		}
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU)==GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU)
		{
			if(pl.getOverTimeState()==GameConstant.PALYER_GAME_OVERTIME_STATE_IN_TABLE_NOWAITING_TO_OVERTIMECHU)
				return;
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//
			msg.operation = GameConstant.MAHJONG_OPERTAION_CHU;

			logger.info("当前玩家="+pl.getPlayerName()+" player_op_chu"+",tableID="+pl.getTableID());
			//uncertainty
            /*player_op_chu(gr,gt,msg,pl);*/

		}
		////玩家吃牌
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_CHI)==GameConstant.MAHJONG_OPERTAION_CHI)
		{
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//uncertainty
			//清理掉刚才的杠
            /*gt.setGangPlayer(null);
            player_op_chi(gr,gt,msg,pl);*/

		}
		////玩家碰牌
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_PENG)==GameConstant.MAHJONG_OPERTAION_PENG)
		{
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//CHAOS_ZHANG modify Player => XiantaoPlayer
			//CHAOS_ZHANG player_op_peng(gr,gt,msg,(XiantaoPlayer)pl);
			//player_op_peng(gr,gt,msg,pl);
		}

		////玩家杠牌，不管啥杠，客户端都发的这个明杠参数，服务器自己会判断是什么杠，客户端用这个MAHJONG_OPERTAION_MING_GANG
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_MING_GANG)==GameConstant.MAHJONG_OPERTAION_MING_GANG)
		{
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//uncertainty
            /*player_op_gang(gr,gt,msg,pl);*/

		}
		////玩家去消吃碰牌
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_CANCEL)==GameConstant.MAHJONG_OPERTAION_CANCEL)
		{
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//uncertainty
			/*player_cancel_chi_peng_gang_hu(gr,gt,msg,pl);*/
		} else if((msg.operation&GameConstant.MAHJONG_OPERTAION_HU)==GameConstant.MAHJONG_OPERTAION_HU)
		{
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//uncertainty
            /*player_op_hu(gt,msg,pl);*/
		}
		else if((msg.operation&GameConstant.MAHJONG_OPERTAION_POP_LAST)==GameConstant.MAHJONG_OPERTAION_POP_LAST){
			AsyncLogicHandleThread thread = this.getLogicThreadByGid(gt);
			if(thread != null){//走异步线程
				this.pushOperationMsg(pl, msg, gt, gr, thread);
				return;
			}
			//uncertainty
			//取得杠牌后抓的尾牌
            /*player_op_get_gang_card(gr,gt,msg,pl);*/
		}

		// 清理最近取消碰的牌
		pl.clearLastCancelPengCard();
	}

	//CHAOS_ZHANG xiantao rule end




	//重连回来没有色的提示
	//不能碰自己的清楚色
	public void updateRoom(SystemConfigPara cfg) {
		if(cfg.getParaID()>= GameConfigConstant.CONF_ROOM_OF_PRIMARY&& cfg.getParaID()<2050)
		{
			boolean found=false;

			for(int i=0;i<gameRoomList.size();i++)
			{
				GameRoom gr=gameRoomList.get(i);
				//
				if(gr.getCfgId()==cfg.getParaID())
				{
					//
					found=true;
					//
					gr.setPrice(cfg.getValueInt()); //底注
					gr.setMaxGold(cfg.getPro_3());
					gr.setMinGold(cfg.getPro_2());
					gr.setRoomType(cfg.getPro_1());
					gr.setServiceFee(cfg.getPro_4());   //台费

					if(gr.getRoomType() == GameConstant.ROOM_TYPE_VIP)
					{
						gr.setFixedGold(cfg.getPro_5());
					}
					//
					if(gr.getPrice() == 0)//0的意思是把这个房间关闭
					{
						gameRoomList.remove(i);
					}
					//
					break;
				}
			}
			//
			//
			if(found==false)
			{
				GameRoom gr=new GameRoom();

				gr.setPrice(cfg.getValueInt()); //底注
                gr.setMaxGold(cfg.getPro_3());
                gr.setMinGold(cfg.getPro_2());
                gr.setRoomType(cfg.getPro_1());
                gr.setServiceFee(cfg.getPro_4());   //台费

				if(gr.getRoomType() == GameConstant.ROOM_TYPE_VIP)
				{
                    gr.setFixedGold(cfg.getPro_5());	//VIP房间固定带入金币数

				}
				//
				if(gr.getPrice()>0)//0的意思是把这个房间关闭
				{
					gameRoomList.add(gr);
				}
			}
		}
	}

	public GameTable popFreeTable() {
		GameTable gt=new GameTable();


		System.out.println("create table="+gt.getTableID());
		//
		return gt;
	}

	public GameRoom getRoom(int roomID) {
		return gameRoomMap.get(roomID);

	}

	public int getRoomPlayerNum(int roomID) {
		GameRoom gr= gameRoomMap.get(roomID);
		if(gr!=null)
			return gr.getPlayerNum();

		return 0;
	}

	public int getRoomPlayerNumByType(int type)
	{
		return 0;
	}

	public int getPlayerVipTableID(Player pl) {
		int roomID=pl.getRoomID();

		GameRoom gr=getRoom(roomID);
		if(gr==null)
			return 0;

		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null){
			return 0;
		}

		if(gt.isVipTable())
		{
			return gt.getVipTableID();
		}

		return 0;
	}

	public void enterVipRoom(EnterVipRoomMsg msg, Player pl){
		GameRoom rm=null;
		GameTable gt=null;
		//
        int roomID = 2002;
        if(msg.getTableID().equals("enter_room"))
        {
            int vip_table_id=msg.getRoomID();
            //
            rm=gameRoomMap.get(roomID);
            gt=rm.getVipTableByVipTableID(vip_table_id);

            msg.setRoomID(roomID);
        }
		else
		{
			rm=gameRoomMap.get(msg.getRoomID());
			if(rm==null)
			{
				logger.error("enterVipRoom,rm==null, playerIndex=" + pl.getPlayerIndex());
				return;
			}
			//
			gt=rm.getTable(msg.getTableID());
		}
		//
		if(gt==null)
		{
			RequestStartGameMsgAck ack=new RequestStartGameMsgAck();
            ack.setGold(pl.getGold());
			//
            ack.setResult(ConstantCode.VIP_TABLE_NOT_FOUND);
			//进入桌子，返回消息给玩家
			ack.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), ack);


			logger.error("enterVipRoom,gt==null, playerIndex=" + pl.getPlayerIndex());
			return;
		}

		//
        gt.setBaseGold(rm.getPrice());

		//
		int old_tablePos = gt.findTablePosOfPlayer(pl);
		boolean old_table = false;
		if(old_tablePos>=0)//已经存在，离开又进来
		{
			old_table=true;
			//
			pl.setTableID(gt.getTableID());
			pl.setVipTableID(gt.getTableID());

		}else
		{
			if(gt.isFull())
			{
				RequestStartGameMsgAck ack=new RequestStartGameMsgAck();
                ack.setGold(pl.getGold());
				//
                ack.setResult(ConstantCode.VIP_TABLE_IS_FULL);
				//进入桌子，返回消息给玩家
				ack.sessionID=pl.getPlayerLobbySessionID();
				GameSocketServer.sendMsg(pl.getSession(), ack);

				return;
			}

			gt.enterPlayer(pl);
		}
		//如等待玩家中，满了就开始，如果中间断线再进入，就不要这个状态切换
		if(gt.isFull()&& gt.getState()==GameConstant.GAME_TABLE_STATE_WAITING_PLAYER)
		{
			gt.setState(GameConstant.GAME_TABLE_STATE_READY_GO);
		}

		//
        pl.setRoomID(msg.getRoomID());
		pl.setTableID(gt.getTableID());
		pl.setVipTableID(gt.getTableID());
		//
		boolean re_enter=false;
		if(old_table && gt.isFull())
			re_enter=true;

		enterTableDone(pl,gt,re_enter);

        pl.gameLogic = this;
	}

    /**
     *  看看是否有老桌需要他进去，牌局未结束，不能离开
     * @param pl
     * @return
     */
    private boolean checkInOldTable(Player pl) {
        int roomID=pl.getRoomID();
        GameRoom rm=gameRoomMap.get(roomID);
        if(rm==null)
            return false;



        GameTable gt=rm.getTable(pl.getTableID());


        if(gt!=null&& (gt.findTablePosOfPlayer(pl)>=0)){
            if((gt.getState()==GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE
                    ||gt.getState()!=GameConstant.GAME_TABLE_STATE_WAITING_PLAYER))
            {
                if(!validatePlayerGold(rm,pl))
                    return false;
            }

            enterTableDone(pl,gt,true);

            return true;
        }

        return false;
    }

	public boolean validatePlayerGold(GameRoom rm,Player pl) {

		if(rm==null)
			return false;
		//
		//进新桌,需要校验金币
		int minGold = rm.getMinGold();


		//如果是进入VIP房间，只要校验完金币就可以了
		if (rm.getRoomType() == GameConstant.ROOM_TYPE_VIP)
		{
			//返回给玩家，
			RequestStartGameMsgAck ack=new RequestStartGameMsgAck();

			ack.setGold(pl.getGold());
			ack.setRoomID(rm.getRoomID());

			ack.setResult(ConstantCode.CAN_ENTER_VIP_ROOM);
			//
			//可以进入VIP房间
			ack.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), ack);
			return false;
		}


		if(pl.getGold() < minGold)
		{
			//返回给玩家，
			RequestStartGameMsgAck ack=new RequestStartGameMsgAck();
			ack.setGold(pl.getGold());

			ack.setResult(ConstantCode.GOLD_LOW_THAN_MIN_LIMIT);
			//进入桌子失败
			ack.sessionID=pl.getPlayerLobbySessionID();
			GameSocketServer.sendMsg(pl.getSession(), ack);
			return false;
		}else if(pl.getGold() > rm.getMaxGold()){
			//返回给玩家，
			RequestStartGameMsgAck ack=new RequestStartGameMsgAck();
			ack.setGold(pl.getGold());

			ack.setResult(ConstantCode.GOLD_HIGH_THAN_MAX_LIMIT);

			//进入桌子失败
			ack.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), ack);
			return false;
		}


		return true;
	}

	public void playerLeftTable(Player pl) {
		if(pl==null)
			return;

		GameRoom gr=this.getRoom(pl.getRoomID());
		if(gr==null)
			return;
		//
		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null){
			return;
		}

		//玩家不在桌子上
		pl.setOnTable(false);
		//
		if(gt.couldLeft(pl))
		{
			//
			boolean found=gt.removePlayer(pl.getPlayerID());

			if(pl.isRobot()){
				machineService.deathMachine(pl.getPlayerIndex());
			}

			//if(gt.isAllRobot()){
			//gt.remove_robots_table();
			//death_machine(gt);
			//}

			//
			if(found)
			{
				gr.setPlayerNum(gr.getPlayerNum()-1);
				gt.addFreePos(pl.getTablePos());
			}

			//如果vip离开桌子,全部清理
			if(gt.isVipTable() && pl.getPlayerID().equals(gt.getCreatorPlayerID()) )
			{
				PlayerGameOpertaionAckMsg axk=new PlayerGameOpertaionAckMsg();
				axk.opertaionID=GameConstant.GAME_OPERTAION_ROOM_DISMISS;
				axk.sessionID=pl.getPlayerLobbySessionID();
				this.sendMsgToTableExceptMe(gt,axk,pl.getPlayerID());
				//
				gt.clearAll();
			}
			//
			if(gt.getPlayerNum()>0)
			{
				//如果还有玩家，通知他们有玩家离开
				if(found)
				{
					PlayerGameOpertaionAckMsg axk=new PlayerGameOpertaionAckMsg();
					axk.opertaionID=GameConstant.GAME_OPERTAION_PLAYER_LEFT_TABLE;
					axk.playerName=pl.getPlayerName();
					axk.gold=pl.getGold();
					axk.headImg=pl.getHeadImg();
					axk.sex=pl.getSex();
					axk.playerID=pl.getPlayerID();
					axk.playerIndex=pl.getPlayerIndex();
					axk.canFriend = pl.getCanFriend();
					axk.tablePos = pl.getTablePos();
					axk.ip = pl.getClientIP();

					axk.sessionID=pl.getPlayerLobbySessionID();
					this.sendMsgToTableExceptMe(gt,axk,pl.getPlayerID());
				}

                if(!gt.isVipTable()&&gt.isAllRobot()){
                    machineService.reset_machinetime_current(gt);
                    machineService.newset_machinetime(pl, gt);
                }
			}

			//
			if(gt.shouldRecycleTable())
			{
				//death_machine(gt);
				//空桌回收
				gt.setState(GameConstant.TABLE_STATE_INVALID);
			}

			pl.clearGameState();
		}else
		{
			//玩家正在游戏，保持数据，等待一定时间，等他回来
			if (gt.isVipTable())
			{
				//游戏暂停
				//if( (gt.getState()!=GameConstant.GAME_TABLE_STATE_PAUSED) && (gt.getState() != GameConstant.GAME_TABLE_STATE_WAITING_PLAYER) )
				//{
				//保存当前状态
				//	gt.setBackup_state(gt.getState());

				//	gt.setState(GameConstant.GAME_TABLE_STATE_PAUSED);
				//	Date ct=DateUtil.getCurrentUtilDate();
				//	gt.setPausedTime(ct.getTime());
				//}
			}
			else
			{
				//普通房间设置成托管
				pl.setAutoOperation(1);
			}


			//玩家状态，暂停
			pl.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_PAUSED);

			//
			//玩家离开
			PlayerOperationNotifyMsg msg=new PlayerOperationNotifyMsg();
			msg.operation=GameConstant.MAHJONG_OPERTAION_OFFLINE;
			msg.setPlayerTablePos(pl.getTablePos());
			msg.sessionID=pl.getPlayerLobbySessionID();
			this.sendMsgToTableExceptMe(gt,msg,pl.getPlayerID());

		}


		//debug,如果玩家都离开了，把桌子回收；
		if(gt.isAllPlayerLeft())
		{
			if (gt.isVipTable())
			{
				//不马上结束，保留一段时间
				//vip_table_end(gr, gt);
			}
			else
			{
				gt.clearAll();
				gt.setState(GameConstant.TABLE_STATE_INVALID);
			}
		}

	}

	private void  last_card_notify(GameTable gt){
		Player plx=gt.getCurrentOperationPlayer();
		if(plx==null)
			return;

		PlayerOperationNotifyMsg msg=new PlayerOperationNotifyMsg();

		MahjongCheckResult wait=new MahjongCheckResult();
		wait.playerTableIndex=plx.getTablePos();
		wait.opertaion=0;

		byte b1 = gt.getLast1Card();
//		byte b2 = gt.getLast2Card();

		//提示玩家抓尾巴
		msg.operation =GameConstant.MAHJONG_OPERTAION_POP_LAST;
		msg.setPlayerTablePos(plx.getTablePos());
		msg.cardLeftNum=gt.getCardLeftNum();
		msg.target_card = (b1);
		//msg.target_card = plx.getCardGrab();
		msg.setChiFlag(plx.getCardGrab());

		logger.info("last_card_notify, playerName=" + plx.getPlayerName() + " bao card=" + Integer.toHexString(msg.target_card) + " tableID=" + plx.getTableID());

		//
		wait.opertaion=msg.operation;
		wait.chiCardValue = msg.target_card;
		wait.pengCardValue = msg.target_card;
		gt.setWaitingPlayerOperate(wait);
		//
		long ctt=DateUtil.getCurrentUtilDate().getTime();
		plx.setOpStartTime(ctt);

		//
		//发给其他玩家，让他们知道当前轮到谁操作
		PlayerOperationNotifyMsg msg2=new PlayerOperationNotifyMsg();
		msg2.operation=GameConstant.MAHJONG_OPERTAION_TIP;
		msg2.cardLeftNum=gt.getCardLeftNum();
		msg2.setPlayerTablePos(plx.getTablePos());
		this.sendMsgToTable(gt,msg2,1);
		//

		//发消息给玩家，提示他出牌
		msg.sessionID=plx.getPlayerLobbySessionID();
		SystemConfig.gameSocketServer.sendMsg(plx.getSession(), msg);
//		logger.info("last_card_notify b1=" + Integer.toHexString(b1) + " b2=" + Integer.toHexString(b2) + " tableID=" + plx.getTableID());
	}

	//检测是否玩家输光，第一局玩家扣金币
	private boolean vip_table_check(GameTable gt) {
		List<Player>  plist=gt.getPlayers();
		//vip房间第一把牌,把数据清理下，
		if(gt.isVipTable() && gt.getHandNum()==0)
		{
			GameRoom gr=getRoom(gt.getRoomID());
			if(gr==null){
				gt.setState(GameConstant.TABLE_STATE_INVALID);
				return false;
			}

			//
			for(int i=0;i<plist.size();i++)
			{
				Player pl=plist.get(i);

				pl.clearVipTableInfo();

				//金币带到桌子上
                pl.setHuFanNum(0);
				pl.setVipTableGold(0);
				//输赢数据清零
				pl.setWinLoseTotal(0);
				pl.setWinLoseGoldNum(0);


			}
		}
		return true;
	}

	//每局扣服务费
	private void service_fee(GameTable gt) {
		GameRoom gr=this.getRoom(gt.getRoomID());
		//
		if(gr==null ||gr.getServiceFee()<=0)
			return;
		//
		List<Player> plist=gt.getPlayers();
		for(int i=0;i<plist.size();i++){
			Player pl=plist.get(i);
			if(pl.getGold()>gr.getServiceFee()){
				String remark="服务费="+gr.getServiceFee();
				this.playerService.substractPlayerGold(pl, gr.getServiceFee(),
                        LogConstant.OPERATION_TYPE_SUB_GOLD_GAME_SERVICE_FEE, remark);
			}
		}
	}

	public boolean checkMsgSeq(PlayerTableOperationMsg msg,Player pl,GameTable gt) {
        if (pl.isRobot()) {
            return false;
        }
        logger.info("checkMsgSeq,sseq:"+msg.serverSeq+", local sseq:"+gt.getServerSeq(pl.getPlayerID())+",cseq:"+msg.clientSeq+",local cseq:"+gt.getPlayerClientSeq(pl.getPlayerID())+",uid:"+pl.getPlayerIndex()+",operation:"+Integer.toHexString(msg.operation));
        boolean dealMsgFlag = false;
        if(msg.clientSeq !=0){
            if(gt.getPlayerClientSeq(pl.getPlayerID()) + 1 == msg.clientSeq){
                gt.setPlayerClientSeq(pl.getPlayerID(), msg.clientSeq);
            }else if(gt.getPlayerClientSeq(pl.getPlayerID()) + 1 < msg.clientSeq){
                dealMsgFlag = true;
            }else{
                dealMsgFlag = true;
            }

        }
//
        if (msg.serverSeq != 0 && gt.getServerSeq(pl.getPlayerID()) != msg.serverSeq)
        {
//				enter_table_done(pl,gt,true,true);
////				player_recover(gt,pl);
//				return true;
            logger.info("check client seq,client_server_seq:"+msg.serverSeq+", server_server_seq:"+gt.getServerSeq(pl.getPlayerID())+",tableID:"+gt.getTableID()+",idx:"+pl.getPlayerIndex());
            List<NetMsgBase> msgList = gt.getCacheMsg(pl.getPlayerID());
            if(msgList==null||msgList.size() == 0){
                return dealMsgFlag;
            }
            int max_seq = 0,min_seq = 9999999;
            String seq_str = "";
            for (int i = 0; i < msgList.size(); i++)
            {

                NetMsgBase cacheMsg = msgList.get(i);
                seq_str +=cacheMsg.serverSeq+"_";
                if (cacheMsg.serverSeq < min_seq){
                    min_seq = cacheMsg.serverSeq;
                }
                if (cacheMsg.serverSeq > max_seq){
                    max_seq = cacheMsg.serverSeq;
                }
            }
            //丢包太多，直接发全量
            if(msg.serverSeq < min_seq-1){
                if(gt.isFull()){
                    enterTableDone(pl,gt,true,true);
                }else{
                    enterTableDone(pl,gt,false,true);
                }
                logger.info("lost too much, send StartGameAck, srcsseq:"+msg.serverSeq);
                return true;
            }
            //客户端比服务器序列号大，不处理，理论上没这种情况
            if(msg.serverSeq > max_seq){
                return dealMsgFlag;
            }

//			Collections.sort(msgList,msgComparator);
            for (int i = 0; i < msgList.size(); i++)
            {
                NetMsgBase cacheMsg = msgList.get(i);
                //发送丢掉的包
                if (cacheMsg.serverSeq > msg.serverSeq){
                    cacheMsg.clientSeq=gt.getPlayerClientSeq(pl.getPlayerID());
                    this.sendBaseMsgToPlayer(gt, pl, msgList.get(i), 0);
                    logger.info("resend old msg, sseq:"+cacheMsg.serverSeq+",cseq:"+cacheMsg.clientSeq+",uid:"+cacheMsg.uid+",cmd:"+cacheMsg.msgCMD);
                }
            }
            return dealMsgFlag;
        }
        if(msg.serverSeq != 0 && gt.getServerSeq(pl.getPlayerID()) == msg.serverSeq){//如果seq相等，删除缓存队列里面老的消息
            gt.removeOldSeqMsg(msg.serverSeq, pl.getPlayerID());
        }

        return dealMsgFlag;

        //return false;
    }

	private void gameStart(GameTable gt) {
		//vip桌子校验
		if(gt.isVipTable())
		{
			if(vip_table_check(gt)==false)
				return;
		}

		Player sanXiangPaoPl=null;
		int hu_player_num=0;//有几个人胡了，有可能一炮3响
		List<Player>  plist=gt.getPlayers();
		for (int i = 0; i < plist.size(); i++)
		{
			Player pl=plist.get(i);
			pl.setAutoOperation(0);
			if (null != pl)
			{
				pl.setOnTable(true);
			}
			//
			if(pl.isWin())
			{
				hu_player_num++;
				if(hu_player_num==3)
				{
					sanXiangPaoPl=pl.getHuPaoPL();
				}
			}
		}


		//
		Date ct=DateUtil.getCurrentUtilDate();
		long ctt=ct.getTime();
		//把玩家按座位排序
		gt.resetPosition();

		// 设置风
		washCardRule.setContainZi(false);
		// 设置花
        washCardRule.setContainHua(false);

		//先洗牌
        List<Byte> cards = washCardRule.washCard();

		gt.washCards(cards); // 洗牌

        try {
			gt.setCards(ZuoPaiTool.zuoPaiFromFile(cards,zuoPaiFilePath));
			logger.debug("zuopai cards = {}", Arrays.toString(gt.getCards().toArray()));
		} catch (Exception e){
			logger.error("做牌失败", e);
		}

		//
		//收台费
		service_fee(gt);


		//判断是否连庄
		int isDealerAgain = 0;
		//


		//继续当庄
		if( (gt.getCurrentHuPlayerIndex() != -1) && (gt.getDealerPos()==gt.getCurrentHuPlayerIndex()) )
		{
			isDealerAgain = 1;
			gt.setLianZhuangNum(gt.getLianZhuangNum()+1);
		}
		else
		{
			//下一个当庄
//			gt.moveDealer();
		}

		if (gt.getCurrentHuPlayerIndex() != -1) {
			gt.setDealerPos(gt.getCurrentHuPlayerIndex());
		} else {
			gt.moveDealer();
		}


		// 清空出牌数据
		gt.setCurrentCard((byte)0);
		gt.setCardOpPlayerIndex(-1);

		//
		//vip桌子，第一把开房人当庄
		if(gt.isVipTable() && gt.getHandNum()<=1)
		{
			gt.setDealerPos(gt.getPlayerTablePos());
		}
		//
		//
		//
		logger.info("游戏正式开始, quanNum=" + gt.getHandNum() + " tableID=" + gt.getTableID());
		//游戏开始
		gt.setState(GameConstant.GAME_TABLE_WAITING_CLIENT_SHOW_INIT_CARDS);
		gt.setHandStartTime(ctt);
		gt.setHandEndTime(0);
		//玩家全体进入游戏状态
		gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_PLAYING);
		//
		//台费
		//int service_gold = 0;
		//GameRoom gr=this.getRoom(gt.getRoomID());
		//service_gold = gr.serviceFee;

		//玩家操作时间
		int operationTime = systemConfigService.getPara(GameConfigConstant.CONF_OPERATION_OVER_TIME).getValueInt();
		if(operationTime < 5)
		{
			operationTime = 5;
		}

		//发牌
		this.sendCards(gt,0, operationTime, isDealerAgain);
		//
		gt.setCurrentHuPlayerIndex(-1);

		vip_table_log_create(gt);
	}

	private void vip_table_log_create(GameTable gt) {
		Date ct=DateUtil.getCurrentUtilDate();
		List<Player>  plist=gt.getPlayers();
		//打第一把，创建下非房主的房间记录，开一次房，4个人，4条记录
		if(gt.isVipTable() && gt.getHandNum()==1)
		{
			//playerService.use_fangka(gt.get, itemBaseID);
			gt.setGameStartTime(ct);
			//
			for(int i=0;i<plist.size();i++)
			{
				Player pl=plist.get(i);

				//创建一条vip房间记录
				VipRoomRecord vrr=new VipRoomRecord();
				vrr.setStartTime(ct);
				vrr.setRoomID(gt.getTableID());
				vrr.setRoomIndex(gt.getVipTableID());
				vrr.setPlayer1ID(pl.getPlayerID());
				vrr.setHostName(gt.getCreatorPlayerIndex()+"-"+gt.getCreator().getPlayerNameNoEmoji());
				//
				int num=0;
				for(int j=0;j<plist.size();j++)
				{
					Player plj=plist.get(j);
					vrr.setRoomType(plj.getRoomID());
					if(plj.getPlayerID().equals(pl.getPlayerID())==false)
					{
						if(num==0){
							vrr.setPlayer2Name(plj.getPlayerIndex() + "-" + plj.getPlayerNameNoEmoji());
							vrr.setScore2(plj.getWinLoseTotal());
						}
						else if(num==1){
							vrr.setPlayer3Name(plj.getPlayerIndex() + "-" + plj.getPlayerNameNoEmoji());
							vrr.setScore3(plj.getWinLoseTotal());
						}
						else if(num==2){
							vrr.setPlayer4Name(plj.getPlayerIndex() + "-" + plj.getPlayerNameNoEmoji());
							vrr.setScore4(plj.getWinLoseTotal());
						}
						//
						num++;
					}
				}
				//
				vrr.setRecordID(UUIDGenerator.generatorUUID());
				//
				playerService.createVipRoomRecord(vrr);
				//
				pl.setVrr(vrr);

			}
		}
	}

	private void search_vip_room(PlayerTableOperationMsg msg,Player pl) {
		int roomID=msg.cardValue;//room id存在cardValue中
		GameRoom gr=this.getRoom(roomID);
		if(gr==null)
			return;
		//
		GameTable gt=null;
		//
		if(msg.opValue==0)
		{
			gt=gr.getTable(pl.getVipTableID());
			if(gt==null)
				return;//默认查找自己开的房间，如果没有，不返回数据
		}else
		{
			gt=gr.getVipTableByVipTableID(msg.opValue);
		}
		//
		SearchVipRoomMsgAck axk=new SearchVipRoomMsgAck();
		//
		if(gt!=null)
		{
			//房主回来
			if(gt.getCreatorPlayerID().equals(pl.getPlayerID()))
			{
				EnterVipRoomMsg msgx=new EnterVipRoomMsg();
				msgx.setTableID(gt.getTableID());
				msgx.setPsw(gt.getVipPswMd5());
				msgx.setRoomID(roomID);
				enterVipRoom(msgx, pl);
				return;
			}
			else
			{
				//判断是否其他玩家离开又回来
				int old_idx=gt.findTablePosOfPlayer(pl);

				if(old_idx>=0)//已经存在，离开又进来
				{
					//直接让他进入房间
					EnterVipRoomMsg msgx=new EnterVipRoomMsg();
                    msgx.setTableID(gt.getTableID());
                    msgx.setPsw(gt.getVipPswMd5());
                    msgx.setRoomID(roomID);
					enterVipRoom(msgx, pl);
					return;
				}
			}
			axk.vipTableID=gt.getVipTableID();
			axk.psw=gt.getVipPswMd5();
			axk.numPlayer=gt.getPlayerNum();
			axk.tableID=gt.getTableID();
			axk.dizhu=gr.getPrice();
			axk.createName=gt.getCreatorPlayerName();
			axk.minGold=gr.getFixedGold();
			axk.newPlayWay=gt.getVipRule();
		}
		axk.sessionID=pl.getPlayerLobbySessionID();
		SystemConfig.gameSocketServer.sendMsg(pl.getSession(), axk);

	}

	//vip续卡
	private void extend_vip_table(PlayerTableOperationMsg msg,Player pl) {
		GameRoom gr=this.getRoom(pl.getRoomID());
		if(gr==null)
			return;

		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null){
			return;
		}
		//
		int diamond_need=playerService.getTableDiamondCost(gt.getBaseHandsTotal());
		//t_global pro_4设置每局需要多少房卡
		//续卡
		if(msg.opValue ==0)
		{
			SystemConfigPara config = systemConfigService.getPara(GameConfigConstant.CONF_IS_VIP_FREE);

			if(pl.getDiamond()>=diamond_need || config.getValueInt() == 1)
			{
				if (playerService.preUserRoomCard(pl, diamond_need))
				{

					gt.setHandsTotal(gt.getHandsTotal()+gt.getBaseHandsTotal());

					gt.setPayVipKa(false);

					//续卡成功
					PlayerOperationNotifyMsg msxg=new PlayerOperationNotifyMsg();
					msxg.operation=GameConstant.MAHJONG_OPERTAION_EXTEND_CARD_SUCCESSFULLY;
					//SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msxg);

					msxg.sessionID=pl.getPlayerLobbySessionID();
					this.sendMsgToTable(gt,msxg);

					logger.info("玩家ID:"+ pl.getPlayerIndex() + "续房成功");

					return;
				}
			}

			//提醒房卡不足
			PlayerOperationNotifyMsg msxg=new PlayerOperationNotifyMsg();
			msxg.operation=GameConstant.MAHJONG_OPERTAION_EXTEND_CARD_FAILED;
			msxg.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msxg);

			logger.info("玩家ID:"+ pl.getPlayerIndex() + "续卡，但房卡不足");
		}
		else
		{
			logger.info("玩家ID:"+ pl.getPlayerIndex() + "不续房");
		}

		//如果VIP圈数到了，就结束房间
		if(gt.isVipTableTimeOver() )
		{
			logger.info("VIP圈数到了,不续卡或房卡不足，VIP房间结束 gt.getState() = " + gt.getState() + ",gt.getBackupState()" + gt.getBackupState());

			if (gt.getState()==GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE
					|| (gt.getState() == GameConstant.GAME_TABLE_STATE_PAUSED && gt.getBackupState() == GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE)
					)
			{
				logger.info("玩家ID:"+ pl.getPlayerIndex() + "不续卡或房卡不足，VIP房间:" + gt.getVipTableID() +"结束");

				//不续卡，或者房卡不足，直接结束房间
				vip_table_end(gr,gt);
			}
		}
	}

	private void cleanTable(GameTable gt){
		if(gt!=null)
		{
			logger.error("房间ID:" + gt.getRoomID() + " 桌子ID：" + gt.getTableID()+","+gt.getVipTableID() + " 桌子状态State：" + gt.getState() + " 当前人数：" + gt.getPlayerNum());
			logger.error("本局开始时间：" + new Date(gt.getHandStartTime())  + " 当前操作人currentOpertaionPlayerIndex:" + gt.getCurrentOpertaionPlayerIndex());
			for(Player ply:gt.getPlayers())
			{
				if (null != ply)
				{
					String cards="";
					for(Byte card:ply.getCardsInHand())
					{
						cards += card;
						cards += "-";
					}

					//logger.error("玩家"+ ply.getPlayerIndex() + " 座位号：" + ply.getTablePos() + " 手牌：" + cards+"，吃碰:"+ply.getDescCardsDown()+" 宝："+gt.getRiffraffCard()+" 摸牌:"+ply.getCardGrab());
					//logger.error(" 玩家打下的牌:"+ply.getCardsBefore());

					//清理要把所有vip设置回0
                    ply.setHuFanNum(0);
					ply.setVipTableGold(0);
				}
			}

			gt.clearGameState();
			gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);
			gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
			gt.setHandEndTime(System.currentTimeMillis());

			//设为非法状态，系统会自动清理
			gt.setState(GameConstant.TABLE_STATE_INVALID);
		}
		else
		{
			logger.info("找不到桌子，不需要结束");
		}
	}

    private void close_vip_table(PlayerTableOperationMsg msg,Player pl) {
        GameRoom gr=this.getRoom(pl.getRoomID());
        if(gr==null)
            return;

        GameTable gt=gr.getTable(pl.getTableID());
        if(gt==null){
            return;
        }

        if(msg.opValue==1 && gt.getCloseVipRoomStartTime() != 0){//已经是解散中状态了，其他请求解散的消息不处理
            return;
        }
        if(gt.getCloseVipRoomStartTime() == 0 && msg.opValue != 1){
            return;
        }

        logger.info("玩家"+ pl.getPlayerIndex()+",解散VIP房间:" + gt.getVipTableID()+",opValue:"+msg.opValue+",gtid:"+pl.getTableID());
        for(int i=0;i<gt.getAgreeCloseRoomPos().size();i++)
        {
            if(pl.getTablePos() == gt.getAgreeCloseRoomPos().get(i)){
                logger.info("玩家已经同意过了,plx:"+pl.getPlayerIndex()+"roomId:"+gt.getVipTableID()+",gtid:"+pl.getTableID());
                return;
            }
        }
        for(int i=0;i<gt.getRefuseCloseRoomPos().size();i++)
        {
            if(pl.getTablePos() == gt.getRefuseCloseRoomPos().get(i)){
                logger.info("玩家已经拒绝过了,plx"+pl.getPlayerIndex());
                return;
            }
        }



        //
        if(msg.opValue==1)//1是请求解散，2是同意解散
        {
            logger.error("玩家"+ pl.getPlayerIndex()+",请求解散VIP房间:" + gt.getVipTableID());
            //有一个请求，前面的请求就清理掉
            gt.clearCloseVipRoomMap();
            //
            PlayerGameOpertaionAckMsg msgx=new PlayerGameOpertaionAckMsg();
            msgx.playerName=pl.getPlayerName();
            msgx.opertaionID=GameConstant.MAHJONG_OPERTAION_WAITING_OR_CLOSE_VIP;
            msgx.playerIndex=pl.getPlayerIndex();

            this.sendMsgToTableExceptMe(gt,msgx,pl.getPlayerID(),1);

            //设置解散开始时间
            Date ct=DateUtil.getCurrentUtilDate();
            long ctt=ct.getTime();
            gt.setCloseVipRoomStartTime(ctt);
            gt.setRequestCloseRoomPlayerPos(pl.getTablePos());
        }
        else if(msg.opValue==2)
        {
            logger.error("玩家"+ pl.getPlayerIndex()+",同意解散VIP房间:" + gt.getVipTableID());
        }

        if(msg.opValue == 3){//点取消
            gt.addVipRoomCloseRequesteCancel(pl.getTablePos());
        }else {
            gt.addVipRoomCloseAgree(pl.getTablePos());
        }
        //
        // 现在1人提成，2人同意，马上解散
        if (gt.getCloseVipRoomRequesterNum() >= 4) {
            logger.error("同意人数达到4人，解散VIP房间:" + gt.getVipTableID()+",gtid:"+gt.getTableID());
            this.sendCloseVipTableAck(gt,true,null);
            //
            //房间解散，当前牌局分数算上
            List<Player>  plist=gt.getPlayers();
            if(gt.isVipTable())
            {
                for(int i=0;i<plist.size();i++)
                {
                    Player p=plist.get(i);
                    //带正负号
                    p.setWinLoseTotal(p.getWinLoseTotal()+p.getWinLoseGoldNum());
                }
            }

            game_over_update_vip(gt);
            //vip_table_end_log(gt);//牌桌解散，吧当前牌局结果写会db
            //结束VIP房间
            vip_table_end(gr,gt);
            gt.clearCloseVipRoomMap();
            gt.clearCloseVipRoomCalcelMap();
            gt.setCloseVipRoomStartTime(0);
            return;
        }

        //有1个人取消，房间不能解散
        if(gt.getCloseVipRoomRequesterCancelNum() >= 1){
            logger.info("cancel people num >1 can not close");
            gt.setCloseVipRoomStartTime(0);
            this.sendCloseVipTableAck(gt,true,null);
            gt.clearCloseVipRoomMap();
            gt.clearCloseVipRoomCalcelMap();

        }else{
            this.sendCloseVipTableAck(gt,true,null);
        }

    }

	//检查vip房间是否要关闭
	private boolean check_close_vip_room(GameTable gt) {
		Date ct=DateUtil.getCurrentUtilDate();
		long ctt=ct.getTime();

		//300秒后没人取消，解散房间
		long close_t = systemConfigService.getPara(GameConfigConstant.CONF_VIP_ROOM_CLOSE_WAIT_TIME).getValueInt() * 1000;
		if (gt.getCloseVipRoomStartTime() != 0 && ctt-gt.getCloseVipRoomStartTime() > close_t && gt.getCloseVipRoomRequesterCancelNum() == 0){
			GameRoom gr=this.getRoom(gt.getRoomID());
			if(gr==null)
				return false;
			logger.error("超时未处理，解散VIP房间:" + gt.getVipTableID());
			//结束VIP房间
			vip_table_end(gr,gt);
			return true;
		}
		return false;
	}

	/**房主申请解散VIP房间*/
	public void applyCloseVipTable(Player pl) {
		GameRoom gr=this.getRoom(pl.getRoomID());
		if(gr==null)
		{
			logger.error("房主:"+ pl.getPlayerIndex()+",申请解散VIP房间 gr==null");
			return;
		}

		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null)
		{
			logger.error("房主"+ pl.getPlayerIndex()+",申请解散VIP房间 gt==null");
			return;
		}

		if(gt.getHandNum()>1){
			logger.error("房主"+ pl.getPlayerIndex()+",申请解散VIP房间,游戏局数大于1");
			return;
		}


		if(gt.getCreatorPlayerID().equals(pl.getPlayerID()))
		{
			//结束VIP房间
			vip_table_end(gr,gt);
			logger.error("房主"+ pl.getPlayerIndex()+",申请解散VIP房间:" + gt.getVipTableID() + ",成功!");
		}else
		{
			//离开房间
			player_left_vip_room(pl,gt,gr);
		}
	}

	//
	private void player_left_vip_room(Player pl,GameTable gt,GameRoom gr) {

		//
		int vipgold=pl.getVipTableGold();
		if(vipgold>0 && vipgold<gr.getFixedGold())//他的vip金币异常，不让退出
		{
			logger.error("玩家"+ pl.getPlayerIndex()+",离开VIP房间:" + gt.getVipTableID() +"，操作失败，vip金币="+vipgold);
			return;
		}
		//
		int state=gt.getState();
		//如果是vip房，一旦开始游戏，就不能离开
		if(gt.isVipTable() && state!=GameConstant.GAME_TABLE_STATE_WAITING_PLAYER)
		{
			logger.error("玩家"+ pl.getPlayerIndex()+",离开VIP房间:" + gt.getVipTableID() +"，操作失败，桌子状态不是在等待中");

			return;
		}

		//


		if(state==GameConstant.GAME_TABLE_STATE_PLAYING
				||state==GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN
				||state==GameConstant.GAME_TABLE_STATE_PAUSED
				||state==GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE)
		{
			logger.error("玩家"+ pl.getPlayerIndex()+",离开VIP房间:" + gt.getVipTableID() +"，操作失败，桌子状态="+state);
			return ;
		}

		//
		//玩家不在桌子上
		pl.setOnTable(false);
		//
		boolean found=gt.removePlayer(pl.getPlayerID());
		if(found)
		{
			if(found)
			{
				gr.setPlayerNum(gr.getPlayerNum()-1);
				gt.addFreePos(pl.getTablePos());
			}
			//
			if(gt.getPlayerNum()>0)
			{
				//如果还有玩家，通知他们有玩家离开
				PlayerGameOpertaionAckMsg axk=new PlayerGameOpertaionAckMsg();
				axk.opertaionID=GameConstant.GAME_OPERTAION_PLAYER_LEFT_TABLE;
				axk.playerName=pl.getPlayerName();
				axk.gold=pl.getGold();
				axk.headImg=pl.getHeadImg();
				axk.sex=pl.getSex();
				axk.playerID=pl.getPlayerID();
				axk.playerIndex=pl.getPlayerIndex();
				axk.canFriend = pl.getCanFriend();
				axk.tablePos = pl.getTablePos();
				axk.ip = pl.getClientIP();

				axk.sessionID=pl.getPlayerLobbySessionID();
				this.sendMsgToTableExceptMe(gt,axk,pl.getPlayerID());

			}

			//
			pl.clearGameState();
			pl.setVipTableID("");
			pl.setVipTableGold(0);


			logger.info("玩家"+ pl.getPlayerIndex()+",离开VIP房间:" + gt.getVipTableID()+",操作成功" );
		}else
		{
			logger.info("玩家"+ pl.getPlayerIndex()+",离开VIP房间:" + gt.getVipTableID()+",桌子上未找到这个玩家" );
		}
	}

	private void player_op_get_gang_card(GameRoom gr,GameTable gt,PlayerTableOperationMsg msg,Player pl){
		MahjongCheckResult  waiting=gt.getWaitingPlayerOperate();

		if(waiting==null){
			logger.error("no such operation, PlayerIndex=" + pl.getPlayerIndex());
			return;//当前没有在等待某个玩家操作；
		}

		if(waiting.playerTableIndex!=pl.getTablePos()){

			logger.error("player_op_get_gang_card,table position invalid,player_index="+pl.getPlayerIndex());
			return;//当前不是在等这个玩家操作
		}

		//吃，或者吃听
		if((waiting.opertaion&GameConstant.MAHJONG_OPERTAION_POP_LAST)!=GameConstant.MAHJONG_OPERTAION_POP_LAST)
		{
			logger.error("operation invalid");
			return;//当前不能吃；
		}

		int gsc  = msg.cardValue;

		byte b1 = 0;
		if(gsc == 1){
			b1 = gt.getLast1Card();
			gt.popLastCard(1);
            gt.setRiffraffCard(gt.getLast1Card());
            gt.setLazarailloCard(gt.getLast2Card());
		}
		else if(gsc == 2){
			b1 = gt.getLast2Card();
			gt.popLastCard(2);
            gt.setRiffraffCard(gt.getLast1Card());
            gt.setLazarailloCard(gt.getLast2Card());
		}
		logger.info("msg.cardValue=" + Integer.toHexString(msg.cardValue) + " b1=" + Integer.toHexString(b1));

//		if (pl.getPlayerIndex() == 127809) {
//			b1 = (byte)0x25;
//		}
		if(b1 == 0)
			return;

		//设置吃的一组牌，客户端好表现
		msg.opValue=b1;
		//msg.player_table_pos = pl.getTablePos();
        msg.cardValue = ((gt.getLazarailloCard() << 8) | gt.getRiffraffCard());
		this.sendMsgToTable(gt,msg,1);

		//
		gt.setCurrentCard((byte)0);
		gt.setCardOpPlayerIndex(-1);

		pl.setCardGrab(b1);

		MahjongCheckResult hu_xx =null;
		//


        hu_xx = mahjongProcessor.checkWin(b1, pl, gt); //
		if(hu_xx!=null)//胡了
		{
			//判断是否五梅花
			boolean iswumeihua = is_wumeihua(b1,pl,gt,hu_xx);

			if (iswumeihua) {
				PlayerOperationNotifyMsg msg1 = new PlayerOperationNotifyMsg();
				msg1.operation = GameConstant.MAHJONG_OPERTAION_HU;
				msg1.target_card = b1;
//				msg1.unused_0 = GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA;

				waiting.playerTableIndex = pl.getTablePos();
				waiting.targetCard = b1;
				waiting.fanResult |= GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA;
				waiting.opertaion = GameConstant.MAHJONG_OPERTAION_HU
						| GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA;
				gt.setWaitingPlayerOperate(waiting);
//				msg1.sessionID = pl.getPlayerLobbySessionID();
//				SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msg1);
			} else {
				// 杠上开花
				PlayerOperationNotifyMsg msg1 = new PlayerOperationNotifyMsg();
				msg1.operation = GameConstant.MAHJONG_OPERTAION_HU;
				msg1.target_card = b1;
//				msg1.unused_0 = GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;

				waiting.playerTableIndex = pl.getTablePos();
				waiting.targetCard = b1;
				waiting.fanResult |= GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;
				waiting.opertaion = GameConstant.MAHJONG_OPERTAION_HU
						| GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;
				gt.setWaitingPlayerOperate(waiting);
//				msg1.sessionID = pl.getPlayerLobbySessionID();
//				SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msg1);
			}
//			return;
		}


		notifyPlayerPlayCard(gt, true, true);

		//gt.setWaitingPlayerOperate(null);

		//等待客户端播动画
		//Date dt=DateUtil.getCurrentUtilDate();
		//gt.setWaitingStartTime(dt.getTime());
		//gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_PLAYING_GANG_CART_LAST_ANIMATION);
	}

	//五梅花,听的牌是五筒,并且宝牌中有一个五筒
	private boolean is_wumeihua(byte card,Player pl,GameTable gt,MahjongCheckResult res){
		if(card==0x25 || card==0x11)
		{
			boolean flag = false;
			if (card == 0x25) {
				flag = true;
				res.addFanResult(GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA);
			}
			// 小鸡当 五筒 用
			if (card == 0x11) {
                MahjongCheckResult hu_xx = mahjongProcessor.checkWin((byte) 0x25, pl, gt);
				if (hu_xx != null) {
					flag = true;
					res.addFanResult(GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA);
				}
			}
			return flag;
		}
		return false;
	}

	//
	private void wait_player_chu(GameTable gt, MahjongCheckResult  waiting, Player pl)
	{
		logger.info("wait_player_chu, player=" + pl.getPlayerName() + " "+",tableID="+pl.getTableID());
		PlayerOperationNotifyMsg notify_msg=new PlayerOperationNotifyMsg();
		notify_msg.operation=GameConstant.MAHJONG_OPERTAION_CHU;
        notify_msg.playerTablePos = waiting.playerTableIndex;
		notify_msg.sessionID=pl.getPlayerLobbySessionID();
		notify_msg.cardLeftNum=gt.getCardLeftNum();
		//SystemConfig.gameSocketServer.sendMsg(pl.getSession(), notify_msg);
		this.sengMsgToPlayer(gt, pl, notify_msg);

		//发给其他玩家，让他们知道当前轮到谁操作
		PlayerOperationNotifyMsg msg2=new PlayerOperationNotifyMsg();
		msg2.operation=GameConstant.MAHJONG_OPERTAION_TIP;
		msg2.cardLeftNum=gt.getCardLeftNum();
        msg2.playerTablePos = pl.getTablePos();
		this.sendMsgToTable(gt,msg2,1);

		//等待玩家出牌
		MahjongCheckResult chu=new MahjongCheckResult();
		chu.playerTableIndex=waiting.playerTableIndex;
		chu.opertaion=GameConstant.MAHJONG_OPERTAION_CHU;
		int waitFan = getWaitFan(gt);
		chu.addFanResult(waitFan);
		gt.setWaitingPlayerOperate(chu);
	}

	private int getWaitFan(GameTable gt) {
		MahjongCheckResult wait = gt.getWaitingPlayerOperate();

		int fan = 0;
		if (wait != null) {
			if ((wait.fanResult & GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU) == GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU) {
				fan |= GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU;
			}
			if ((wait.fanResult & GameConstant.MAHJONG_HU_CODE_GANG_SHANG_PAO) == GameConstant.MAHJONG_HU_CODE_GANG_SHANG_PAO) {
				fan |= GameConstant.MAHJONG_HU_CODE_GANG_SHANG_PAO;
			}
			if ((wait.fanResult & GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA) == GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA) {
				fan |= GameConstant.MAHJONG_HU_CODE_WU_MEI_HUA;
			}
			if ((wait.fanResult & GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA) == GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA) {
				fan |= GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;
			}
			if ((wait.fanResult & GameConstant.MAHJONG_HU_CODE_FANG_GANG_HU) == GameConstant.MAHJONG_HU_CODE_FANG_GANG_HU) {
				fan |= GameConstant.MAHJONG_HU_CODE_FANG_GANG_HU;
			}
		}

//		logger.info("getWaitFan=" + Integer.toHexString(fan) + " tableID=" + gt.getTableID());
		return fan;
	}

	private void printPlayerLog(Player pl, int op, int opCard)
	{
		if(showCardLog)
		{
			String pop="==null==";
			if(op==GameConstant.MAHJONG_OPERTAION_PENG)
				pop="==PENG==";
			else if(op==GameConstant.MAHJONG_OPERTAION_HU)
				pop="==HU==";
			else if(op==GameConstant.MAHJONG_OPERTAION_MING_GANG)
				pop="==GANG==";
			else if(op==GameConstant.MAHJONG_OPERTAION_CANCEL)
				pop="==CANCEL==";
			else if(op==GameConstant.MAHJONG_OPERTAION_CHU)
				pop="==CHU==";

			String xx=pop+" opCard="+Integer.toHexString(opCard)+","+pl.getPlayerDebugDesc()+",tableID="+pl.getTableID();
			logger.info(xx);
		}
	}

	//
	private void add_card_unique(List<Byte>cds,int cd)
	{
		byte bb=(byte)(cd&0xff);
		int v=cd&0xf;
		if(v<=0 || v>9)
			return;

		// 不添加小鸡
		if (cd == 0x11) {
			return;
		}

		for(int i=0;i<cds.size();i++)
		{
			byte b=cds.get(i);
			if(b==cd)
				return;
			//
			if(cd<=b){
				cds.add(i, bb);
				return;
			}
		}
		//插在末尾
		cds.add(bb);
	}

	private void update_player_hu_card_list(GameTable gt,Player pl) {

		if(pl.isRobot())
			return;
		//
		List<Byte> huList = new ArrayList<Byte>();

		//
		List<Byte> cds = new ArrayList<Byte>();
		List<Byte> cds_inhand = pl.getCardsInHand();

		for(int i=0;i < cds_inhand.size();i++)
		{
			int b = cds_inhand.get(i);

			add_card_unique(cds, b);
			add_card_unique(cds, b - 1);
			add_card_unique(cds, b + 1);
			add_card_unique(cds, b - 2);
			add_card_unique(cds, b + 2);
			add_card_unique(cds, b - 3);
			add_card_unique(cds, b + 3);
		}

		for(int i=0;i<cds.size();i++)
		{
			byte b=cds.get(i);
			MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
			if(hu!=null)
				huList.add(b);
		}

		if (huList.size() == 0) {
			byte bao1 = gt.getRiffraffCard();
            byte bao2 = gt.getLazarailloCard();

			MahjongCheckResult gang = mahjongProcessor.checkGang(gt, (byte)0x70, pl, true);
			if (gang != null) {
				if (gang.gangList.size() == 1) {
					CardDown cd = gang.gangList.get(0);
					int value = cd.cardValue & 0xff;
					int num = pl.getXCardNumInHand(value);
					for (int i = 0; i < num; i++) {
						pl.removeCardInHand(value);
					}
					pl.addCardInHand(bao1);

					for(int i=0;i<cds.size();i++)
					{
						byte b=cds.get(i);
						MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
						if(hu!=null)
							huList.add(b);
					}

					pl.removeCardInHand(bao1);

					if (huList.size() == 0) {
						pl.addCardInHand(bao2);
						for(int i=0;i<cds.size();i++)
						{
							byte b=cds.get(i);
							MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
							if(hu!=null)
								huList.add(b);
						}
						pl.removeCardInHand(bao2);
					}

					for (int i = 0; i < num; i++) {
						pl.addCardInHand((byte)value);
					}
				} else if (gang.gangList.size() == 2) {
					CardDown cd = gang.gangList.get(0);
					CardDown cd2 = gang.gangList.get(1);

					// 双杠能听牌
					int value1 = cd.cardValue & 0xff;
					int value2 = cd2.cardValue & 0xff;
					int num = pl.getXCardNumInHand(value1);
					for (int i = 0; i < num; i++) {
						pl.removeCardInHand(value1);
					}
					int num2 = pl.getXCardNumInHand(value2);
					for (int i = 0; i < num2; i++) {
						pl.removeCardInHand(value2);
					}

					pl.addCardInHand(bao1);
					pl.addCardInHand(bao2);

					for(int i=0;i<cds.size();i++)
					{
						byte b=cds.get(i);
						MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
						if(hu!=null)
							huList.add(b);
					}

					pl.removeCardInHand(bao1);
					pl.removeCardInHand(bao2);

					for (int i = 0; i < num; i++) {
						pl.addCardInHand((byte)value1);
					}
					for (int i = 0; i < num2; i++) {
						pl.addCardInHand((byte)value2);
					}

					// 单杠听牌
					if (huList.size() == 0) {
						for (int i = 0; i < num; i++) {
							pl.removeCardInHand(value1);
						}
						pl.addCardInHand(bao1);

						for(int i=0;i<cds.size();i++)
						{
							byte b=cds.get(i);
							MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
							if(hu!=null)
								huList.add(b);
						}

						pl.removeCardInHand(bao1);

						if (huList.size() == 0) {
							pl.addCardInHand(bao2);

							for(int i=0;i<cds.size();i++)
							{
								byte b=cds.get(i);
								MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
								if(hu!=null)
									huList.add(b);
							}

							pl.removeCardInHand(bao2);
						}

						for (int i = 0; i < num; i++) {
							pl.addCardInHand((byte)value1);
						}
					}


					if (huList.size() == 0) {
						for (int i = 0; i < num2; i++) {
							pl.removeCardInHand(value2);
						}
						pl.addCardInHand(bao1);

						for(int i=0;i<cds.size();i++)
						{
							byte b=cds.get(i);
							MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
							if(hu!=null)
								huList.add(b);
						}

						pl.removeCardInHand(bao1);

						if (huList.size() == 0) {
							pl.addCardInHand(bao2);

							for(int i=0;i<cds.size();i++)
							{
								byte b=cds.get(i);
								MahjongCheckResult hu=mahjongProcessor.checkWin(b, pl, gt);
								if(hu!=null)
									huList.add(b);
							}

							pl.removeCardInHand(bao2);
						}

						for (int i = 0; i < num2; i++) {
							pl.addCardInHand((byte)value2);
						}
					}
				}
			}
		}

		pl.setTingList(huList);

		cds.clear();


	}

	private void sengMsgToPlayer(GameTable gt,Player pl,PlayerOperationNotifyMsg msg) {
		sengMsgToPlayer(gt, pl, msg, 1);
	}

    private void sengMsgToPlayer(GameTable gt,Player pl,PlayerOperationNotifyMsg msg, int cacheMsg) {
        //其他从操作发给自己
        msg.sessionID=pl.getPlayerLobbySessionID();
        if (1 == cacheMsg) {
            GameSocketServer.sendMsgWithSeq(pl.getSession(), msg, pl.getPlayerID(), gt);
        } else {
            GameSocketServer.sendMsg(pl.getSession(), msg);
        }

        if(pl.isRobot())
        {
            machineService.postMsg(gt, pl, msg);
        }
    }

	//按顺序查询下玩家能否进行某个操作
	//chuCardPlayerTableIndex为出牌玩家的位置，一个人出牌，引发多人吃，每次检测到了出牌那个位置，肯定要停止了，不能无限循环
	private MahjongCheckResult table_loop_check(GameTable gt,byte card,Player opPlayer,int checkOp,int chuCardPlayerTableIndex) {
		MahjongCheckResult code=null;

		List<Player> plist=gt.getPlayers();
		int pl_index=opPlayer.getTablePos()+1;
		if(pl_index>=plist.size())
			pl_index=0;
		//
		//

		for(int i=0;i<plist.size()-1;i++)
		{
			Player pl=plist.get(pl_index);

			if(pl.getTablePos()==chuCardPlayerTableIndex)
				break;

			//

			//已经听牌，看看能胡不
			if(checkOp==GameConstant.MAHJONG_OPERTAION_HU)
			{
				//先检测卡胡
				code=mahjongProcessor.checkWin(card, pl,gt);
				if (code != null) {
					int waitFan = getWaitFan(gt);
					code.addFanResult(waitFan);
				}
			}


			//杠
			if(checkOp==GameConstant.MAHJONG_OPERTAION_MING_GANG||checkOp==GameConstant.MAHJONG_OPERTAION_AN_GANG||checkOp==GameConstant.MAHJONG_OPERTAION_BU_GANG)
			{
				code = mahjongProcessor.checkGang(gt,card,pl,false);
			}

			//碰
			if(checkOp==GameConstant.MAHJONG_OPERTAION_PENG)
				code=mahjongProcessor.check_peng(gt,card, pl);

			//
			//可以操作
			if(code!=null)
			{
				//所有操作只有一个玩家有可能进行
				break;
			}
			//
			pl_index++;
			if(pl_index >= plist.size())
				pl_index=0;
		}

		//
		return code;
	}

	private void checkNextPlayerOperation(GameTable gt, byte card, Player opPlayer, int checkOps) {
		if( opPlayer == null)
			return;

		//
		MahjongCheckResult result=null;
		PlayerOperationNotifyMsg msg=new PlayerOperationNotifyMsg();

		do{
			//1先看看有没有胡的玩家
			if((checkOps&GameConstant.MAHJONG_OPERTAION_HU)==GameConstant.MAHJONG_OPERTAION_HU)
			{
				result = table_loop_check(gt,card,opPlayer,GameConstant.MAHJONG_OPERTAION_HU,opPlayer.getTablePos());
				if(result!=null)
				{
					int waitFan = getWaitFan(gt);
					result.addFanResult(waitFan);
					if (result != null) {
						//这个是点炮的玩家
						result.chuPlayer = opPlayer;
						//
						msg.operation = GameConstant.MAHJONG_OPERTAION_HU;
						msg.target_card = result.targetCard;
						break;
					}
				}
			}

			//看看有没有玩家杠
			if((checkOps&GameConstant.MAHJONG_OPERTAION_MING_GANG)==GameConstant.MAHJONG_OPERTAION_MING_GANG)
			{
				result=table_loop_check(gt,card,opPlayer,GameConstant.MAHJONG_OPERTAION_MING_GANG,opPlayer.getTablePos());
				if(result!=null)
				{
					//
					msg.pengCardValue=result.pengCardValue;
					//杠的同时，也可以碰
					//result.opertaion |=GameConstant.MAHJONG_OPERTAION_PENG;
					//记录那个出的牌，这样杠算钱也好算
					result.chuPlayer=opPlayer;
					//

					msg.operation |= result.opertaion;
					msg.gangList = result.gangList;
					//杠的同时，肯定可以碰，也提示了
					//msg.operation |= GameConstant.MAHJONG_OPERTAION_MING_GANG;

					MahjongCheckResult pengresult=table_loop_check(gt,card,opPlayer,GameConstant.MAHJONG_OPERTAION_PENG,opPlayer.getTablePos());

					if(pengresult!=null)
					{
						msg.pengCardValue=pengresult.pengCardValue;
						result.pengCardValue = msg.pengCardValue;
						//
						msg.operation |=GameConstant.MAHJONG_OPERTAION_PENG;
						//
						//碰杠，检查下家是否可以吃
						Player plx=gt.getCurrentOperationPlayer();
						if(plx!=null && plx.getTablePos()==pengresult.playerTableIndex)
						{
							MahjongCheckResult chi_result=mahjongProcessor.check_chi(gt,card, plx);
							if(chi_result!=null)
							{
								msg.operation |=GameConstant.MAHJONG_OPERTAION_CHI;
								result.opertaion |=GameConstant.MAHJONG_OPERTAION_CHI;
								msg.chiCardValue=chi_result.chiCardValue;
								result.chiCardValue=chi_result.chiCardValue;//后面读取的是result的值
								break;
							}
						}
						break;
					}

					break;
				}
			}
			//2看看有没有玩家可以碰
			if((checkOps&GameConstant.MAHJONG_OPERTAION_PENG)==GameConstant.MAHJONG_OPERTAION_PENG)
			{
				result=table_loop_check(gt,card,opPlayer,GameConstant.MAHJONG_OPERTAION_PENG,opPlayer.getTablePos());
				if(result != null)
				{
					msg.pengCardValue=result.pengCardValue;

					//
					msg.operation |=GameConstant.MAHJONG_OPERTAION_PENG;
					//
					//检查下家是否可以吃
					Player plx=gt.getCurrentOperationPlayer();
					if(plx!=null && plx.getTablePos()==result.playerTableIndex)
					{
						MahjongCheckResult chi_result=mahjongProcessor.check_chi(gt,card, plx);
						if(chi_result!=null)
						{
							msg.operation |=GameConstant.MAHJONG_OPERTAION_CHI;
							result.opertaion |=GameConstant.MAHJONG_OPERTAION_CHI;
							msg.chiCardValue=chi_result.chiCardValue;
							result.chiCardValue=chi_result.chiCardValue;//后面读取的是result的值
							break;
						}
					}
					break;
				}
			}

			if((checkOps&GameConstant.MAHJONG_OPERTAION_CHI)==GameConstant.MAHJONG_OPERTAION_CHI)
			{
				//如果没有吃碰杠，检查下家是否可以吃
				Player plx=gt.getCurrentOperationPlayer();

				result=mahjongProcessor.check_chi(gt,card, plx);
				if(result!=null)
				{
					msg.operation=GameConstant.MAHJONG_OPERTAION_CHI;
					break;
				}
				//
			}
//			if (result != null) {
//				break;
//			}
			//
		}while(false);
		//
		if(result!=null)
		{
			//设置当前桌子的当地玩家操作，等玩家操作的时候，再核查一下
			result.opertaion=msg.operation;//以msg的操作为准；
			//
			gt.setWaitingPlayerOperate(result);
			//
			msg.chiCardValue=result.chiCardValue;
			msg.pengCardValue=result.pengCardValue;
			msg.setPlayerTablePos(result.getPlayerTableIndex());
			msg.target_card=result.targetCard;
			msg.cardLeftNum=opPlayer.getTablePos();		//吃、碰、听谁的,借用这个字段
			//
			//
			Player pl=gt.getPlayerAtIndex(msg.getPlayerTablePos());

			//设置操作开始时间
			long ctt=DateUtil.getCurrentUtilDate().getTime();
			pl.setOpStartTime(ctt);


			//其他从操作发给自己
			msg.sessionID=pl.getPlayerLobbySessionID();
			//SystemConfig.gameSocketServer.sendMsg(pl.getSession(), msg);
			this.sengMsgToPlayer(gt, pl, msg);
		}
		else
		{
			//4如果什么操作都没有，下个玩家进行摸牌动作
			grabCard(gt);
		}
	}


	/**
	 * 当前玩家摸牌
	 *
	 * @param gameTable
	 */
	@Override
	public void grabCard(GameTable gameTable) {
		Player player = gameTable.getCurrentOperationPlayer();
		if (player == null) {
			logger.error("玩家摸牌失败,当前操作玩家不存在,tableId:{}", gameTable.getTableID());
			return;
		}
		// 发给其他玩家，让他们知道当前轮到谁操作
		PlayerOperationNotifyMsg msg = new PlayerOperationNotifyMsg();
		msg.setOperation(com.szxx.constant.GameConstant.MAHJONG_OPERTAION_TIP);
		msg.setCardLeftNum(gameTable.getCardLeftNum());
		msg.setPlayerTablePos(player.getTablePos());
		this.sendMsgToTable(gameTable, msg, 1);

		player.setLastGangChuPlayer(null);
		// 有人摸牌了，前面的杠不需要记录了
		gameTable.setGangPlayer(null);
		player.setCancelHu(false);
		player.setCouldNotChuCards(0);
		// 去掉可能的被抢杠前的记录
		gameTable.setGangOpBackup(null);
		gameTable.setGangMsgBackup(null);

		for(Player p : gameTable.getPlayers()){
			p.setCancelHu(false);
		}

		if(gameTable.isEnd()){//记录牌局一帧
			gameTable.addTableFrame(null, 0, com.szxx.constant.GameConstant.MAHJONG_OPERTAION_CHU);
		}else{
			gameTable.addTableFrame(null, gameTable.getFirst1Card(), com.szxx.constant.GameConstant.MAHJONG_OPERTAION_CHU);
		}

		// 在玩家摸牌之前，把上家的打出来的牌，放到玩家面前（因为没有吃碰胡）
		chu_end(gameTable);

		// 服务器清除等待玩家操作的数据
		gameTable.setWaitingPlayerOperate(null);

		// 剩余四张牌
		if (gameTable.getCardLeftNum() - gameTable.getMaiMaNum() == 4) {
			PlayerOperationNotifyMsg last4CardMsg = new PlayerOperationNotifyMsg();
			last4CardMsg.operation = com.szxx.constant.GameConstant.MAHJONG_OPERTAION_SHOW_TABLE_TIPS;
			last4CardMsg.target_card = 1;
			last4CardMsg.cardLeftNum = gameTable.getCardLeftNum();
			this.sendMsgToTable(gameTable, last4CardMsg, 1);
		}

		// 摸光为止
		if (gameTable.isEnd()) {
			game_over_hu(gameTable, true);
		} else {
			// 手里13张
			if (player.getCardNumInHand() % 3 == 1 && player.getCardGrab() == 0) {
				// 给玩家摸一张
				byte b = gameTable.popCard();
				logger.info(player.getPlayerName() + " player_mo, card=" + Integer.toHexString(b) + ",tableID="
						+ player.getTableID());

				player.setCardGrab(b);
				// 记录摸牌人的座位
				gameTable.setLastMoCardPlayerTablePos(player.getTablePos());
				//
				player.setMoCardNum(player.getMoCardNum() + 1);
				notifyPlayerPlayCard(gameTable, true, true);

			} else {
				notifyPlayerPlayCard(gameTable, true, true);
			}
		}

	}

	//下精分
	private void gang_bao1_calculate(GameTable gt) {
		//
		int dizhu=gt.getBaseGold();
		List<Player> plist=gt.getPlayers();


		int secondBao=gt.getSecondBaoCard();
		int hasShangJinPlNum=0;
		for(int i=0;i<plist.size();i++)
		{
			Player pl=plist.get(i);

			int score1=pl.getCardXNumAll(gt.getRiffraffCard());
			int score2=pl.getCardXNumAll(secondBao);

			pl.setBao1Score(score1*2+score2);
			if(score1>0 || score2>2)
				hasShangJinPlNum++;
			//
		}


		for(int i=0;i<plist.size();i++)
		{
			Player pl=plist.get(i);
			int score=pl.getBao1Score();
			if(score==0)
				continue;
			//
			if(score==5)score=10;
			else if(score==6)score=18;
			else if(score==7)score=28;
			else if(score==8)score=40;
			else if(score==9)score=54;
			else if(score==10)score=70;
			//
			if(hasShangJinPlNum==1)
				score*=2;

			pl.setBao1Score(score*dizhu);
		}


	}

	//
	private void gang_score_calculate(GameTable gt) {

		List<Player> plist=gt.getPlayers();
		for(int i=0;i<plist.size();i++)
		{
			Player pl=plist.get(i);
			pl.setGangMoneyNum(0);
		}

		//
		int dizhu=gt.getBaseGold();
		//


		for(int i=0;i<plist.size();i++)
		{
			Player pl=plist.get(i);


			int score=0;
			//
			score=pl.getGangScore(dizhu,gt.getRiffraffCard(),gt.getSecondBaoCard())*dizhu;
			//
			if(score>0)
			{
				int sc=score/3;//其他人一人扣1/3
				for(int j=0;j<plist.size();j++)
				{
					Player plx=plist.get(j);
					if(plx.getPlayerID().equals(pl.getPlayerID()))
						continue;
					//
					//其他玩家扣分
					plx.setGangMoneyNum(plx.getGangMoneyNum()-sc);
				}
				//自己加分
				pl.setGangMoneyNum(pl.getGangMoneyNum()+score);
			}
		}

		for(int j=0;j<plist.size();j++)
		{
			Player plx=plist.get(j);
			if(plx.getGangMoneyNum()>0)
			{
				plx.setHuDesc(plx.getHuDesc()+" 杠分+"+plx.getGangMoneyNum());
			}else if(plx.getGangMoneyNum()<0)
			{
				plx.setHuDesc(plx.getHuDesc()+" 杠分"+plx.getGangMoneyNum());
			}
		}

	}

	//只胡一张
	public boolean is_real_si_jia(GameTable gt,Player pl,byte card) {
		List<Byte> cards=new ArrayList<Byte>();
		List<Byte> cds= pl.getCardsInHand();
		for(int i=0;i<cds.size();i++)
		{
			byte c=cds.get(i);
			byte c_1=(byte)(c-1);
			byte c_2=(byte)(c+1);
			boolean found1=false;
			boolean found_1=false;
			boolean found_2=false;
			//
			int v1=c_1&0xf;
			int v2=c_2&0xf;
			if(v1<=1)
				found_1=true;//不要插入
			if(v2>=9)
				found_2=true;//不要插入
			//

			for(int k=0;k<cards.size();k++)
			{
				byte d=cards.get(k);
				if(c==d)
				{
					found1=true;
				}
				if(c_1==d)
				{
					found_1=true;
				}
				if(c_2==d)
				{
					found_2=true;
				}
			}
			//
			if(found1==false)
				cards.add(c);
			if(found_1==false)
				cards.add(c_1);
			if(found_2==false)
				cards.add(c_2);
		}
		//
		int hu_num=0;
		//
		for(int k=0;k<cards.size();k++)
		{
			byte d=cards.get(k);
			MahjongCheckResult hu=mahjongProcessor.checkWin(d, pl, gt);
			if(hu!=null)
				hu_num++;
			if(hu_num>1)
				return false;
		}
		return true;
	}

	private String get_player_pos_desc(GameTable gt,Player pl) {
		String pos="";
		int offset=gt.getDealerPos()-pl.getTablePos();
		//

		if(offset==-1 || offset==3)//下家
		{
			pos="下家";
		}
		else if(offset==-2 || offset==2)//对家
		{
			pos="对家";
		}else if(offset==-3 || offset==1)//上家
		{
			pos="上家";
		}

		return pos;
	}

	//吃三笔：一家碰或吃或碰杠同一玩家三次，则为吃三笔；
	private boolean isOther3PlayerHasNoJing(GameTable gt, Player pl) {
		int bao2=gt.getSecondBaoCard();
		List<Player> plist=gt.getPlayers();
		for(int i=0;i<plist.size();i++)
		{
			Player px=plist.get(i);
			//
			if(px.getPlayerID().equals(pl.getPlayerID()))
				continue;
			//
			int num=px.getCardXNumAll(gt.getRiffraffCard());
			if(num>0)
				return false;

			num=px.getCardXNumAll(bao2);
			if(num>0)
				return false;
		}

		return true;
	}

	private void win_lose_money_submit(GameTable gt) {
		//杠算分
		List<Player> plist=gt.getPlayers();

		//
		//最后输赢处理

		for(int i=0;i<plist.size();i++)
		{
			Player px=plist.get(i);

			px.setFanNum(px.getWinLoseGoldNum()/gt.getBaseGold());
			if(px.getWinLoseGoldNum()==0)
				continue;



			//
			String remark="输金币="+px.getWinLoseGoldNum()+"描述="+px.getFanDesc();
			//

			if(gt.isVipTable())//扣桌子上的钱
			{
				px.setVipTableGold(px.getVipTableGold()+px.getWinLoseGoldNum());

				remark+=",桌上金币="+px.getVipTableGold();
				if(px.getWinLoseGoldNum()<0)
					playerService.createPlayerLog(px.getPlayerID(), px.getPlayerIndex(), px.getPlayerName(), px.getGold(), LogConstant.OPERATION_TYPE_SUB_GOLD, LogConstant.OPERATION_TYPE_SUB_GOLD_GAME_LOSE, px.getVipTableGold(), remark, LogConstant.MONEY_TYPE_GOLD);
				else
					playerService.createPlayerLog(px.getPlayerID(), px.getPlayerIndex(), px.getPlayerName(), px.getGold(), LogConstant.OPERATION_TYPE_ADD_GOLD, LogConstant.OPERATION_TYPE_ADD_GOLD_GAME_WIN, px.getVipTableGold(), remark, LogConstant.MONEY_TYPE_GOLD);
			}
			else
			{
				if(px.getWinLoseGoldNum()>0)
					playerService.add_player_gold(px, px.getWinLoseGoldNum(), LogConstant.OPERATION_TYPE_ADD_GOLD_GAME_WIN, remark);
				else
					playerService.substractPlayerGold(px, px.getWinLoseGoldNum()*(-1), LogConstant.OPERATION_TYPE_SUB_GOLD_GAME_LOSE, remark);


			}
            logger.info(" remark" + remark);
		}

	}

	private void game_over_update_vip(GameTable gt) {
		List<Player> plist=gt.getPlayers();
		//坐庄次数
		for(int i=0;i<plist.size();i++)
		{
			Player px=plist.get(i);
			if(px.getTablePos() == gt.getDealerPos())
			{
				px.setZhuangCount(px.getZhuangCount()+1);
			}
			//
			if(px.isWin()){
				px.setWinCount(px.getWinCount()+1);
			}
			//
			px.setGangCount(px.getGangCount()+px.getGangNum());
			px.setAnGangNum(px.getAnGangNum()+px.getAnGangCounter());
			px.setMingGangNum(px.getMingGangNum()+px.getGongGangNum());
		}
	}

	//pao_pl是放炮的玩家，如果没有就是null,isKaHu是否是卡胡，isHuBao是不是胡的那种刚好是宝牌，自摸
	private void game_over_hu(GameTable gt,boolean isLiuJu) {
		//不是流局
		if(isLiuJu==false)
		{
			gt.setLiuJuNoPlayerHu(false);
			//
			game_over_update_vip(gt);

		}
		else
		{
			gt.setLiuJuNoPlayerHu(true);

			chajiao(gt);
		}
		//流局不流局，杠必须算分
		//合并计算提交数据库
		win_lose_money_submit(gt);
		//
		gt.setState(GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN);

		//
		long ctt=DateUtil.getCurrentUtilDate().getTime();
		gt.setHandEndTime(ctt);

	}
    //查叫
    private void chajiao(GameTable gt) {
        int manFan = systemConfigService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getValueInt();
        int difen = systemConfigService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getPro_1();

        if(gt.isGoldChangeCommited())
        {
            logger.info("ERROR========game_over_hu call twice=================================");
            return;
        }
        //防止算两次
        gt.setGoldChangeCommited(true);
        //
        game_over_update_vip(gt);

        //
        boolean hasPlNotTing=false;
        boolean hasPlTing=false;

        boolean liu_ju_no_player_hu=true;

        List<Player> plist=gt.getPlayers();

        for(int i=0;i<plist.size();i++)
        {
            Player pl=plist.get(i);
            {
                //看看是否听牌
                boolean ting=checkPlayerTing(pl, gt);
                //
                pl.setTingCard(ting);
                //
                if(ting)
                {
                    //
                    hasPlTing=true;
                    //有人胡牌
                    liu_ju_no_player_hu=false;
                    //
                    pl.setFanType(pl.getFanType()|GameConstant.MAHJONG_HU_CODE_TING);
                }
                else
                {
                    hasPlNotTing=true;
                }
            }
        }

        //
        gt.setLiuJuNoPlayerHu(liu_ju_no_player_hu);
        //如果有人听牌，有人没听牌，计算下听牌的番
        if((hasPlNotTing && hasPlTing))
        {
            for(int k=0;k<plist.size();k++)
            {
                Player plx=plist.get(k);
                //计算下听牌的番
                if(plx.isTingCard())
                {
                    calculate_player_max_fan(gt,plx);
                }
            }
        }
        //
        Map<String,Integer> addScorePlayer = new HashMap<String,Integer>();

        addScorePlayer.clear();
//查叫算分
        for(int i=0;i<plist.size();i++)
        {
            Player plx=plist.get(i);
            //
            if(plx.isTingCard())//
            {
                int total_gold=0;
                int fan=plx.getMaxFanNum();
                int score =difen;
                if(fan>manFan){
                    fan=manFan;
                }
                for(int k=0;k<fan;k++){
                    score = score*2;
                }
                for(int k=0;k<plist.size();k++)//查大叫
                {
                    Player pl=plist.get(k);
                    //未听牌给听牌的钱
                    if(pl.isTingCard()==false)
                    {
                        if(addScorePlayer.get(pl.getPlayerID()) == null){
                            addScorePlayer.put(pl.getPlayerID(), score);
                        }else{
                            addScorePlayer.put(pl.getPlayerID(), addScorePlayer.get(pl.getPlayerID()) + score);
                        }
                        total_gold +=score;

                        String remark="查叫,idx="+plx.getPlayerIndex()+"赢,player="+pl.getPlayerIndex()+"输,gold="+score;

                        pl.setWinLoseGoldNum(pl.getWinLoseGoldNum()-score);
                        pl.setFanType(pl.getFanType()|GameConstant.MAHJONG_HU_CODE_CHA_LIU_JU|GameConstant.MAHJONG_HU_CODE_LOSE);
//						modify_player_gold(gt, pl, -score, remark,"被查叫");

                        logger.info(remark);
                    }
                }
                //
                if(total_gold>0)
                {
                    String remark="查叫,idx="+plx.getPlayerIndex()+"赢,gold="+total_gold;
                    //
                    logger.info(remark+"  胡翻前     "+plx.getHuFanNum()+"  翻数"+fan);
                    plx.setWinLoseGoldNum(plx.getWinLoseGoldNum()+total_gold);
                    plx.setFanType(GameConstant.MAHJONG_HU_CODE_WIN|GameConstant.MAHJONG_HU_CODE_CHA_LIU_JU);
                    //
//					int fanType=GameConstant.MAHJONG_HU_CODE_WIN|GameConstant.MAHJONG_HU_CODE_CHA_LIU_JU;
                    plx.setHuFanNum(plx.getHuFanNum()+total_gold);
                    logger.info(remark+"  胡翻 后    "+plx.getHuFanNum());

                }
            }
        }
        logger.info("game over hu,tableID="+gt.getTableID()+",vipTableID="+gt.getVipTableID()+",hand:"+gt.getHandNum()+",handTotal:"+gt.getHandsTotal());

        //第一局结束扣房卡
        if(gt.isVipTable())
        {
            if(!gt.isPayVipKa())
            {
                if(! playerService.use_fangka(gt)){
                    cleanTable(gt);
                    return;
                }
                gt.setPayVipKa(true);
            }
        }

//		//牌局结束给每个人发个全量
        for(int i=0;i<plist.size();i++)
        {
            Player pl=plist.get(i);
            if (pl != null && pl.isNewVersion()){
                if(gt.isFull()){
                    enterTableDone(pl,gt,true,true);
                }else{
                    enterTableDone(pl,gt,false,true);
                }
            }
        }

        long ctt=DateUtil.getCurrentUtilDate().getTime();
        gt.setHandEndTime(ctt);
    }

    //查找玩家手里之前的杠，不是刚摸起来的杠
	//看看玩家有没有暗杠，或者补杠，找全部
	private MahjongCheckResult find_gang(GameTable gt,Player pl) {
		int minCardNum=4;


		//暗杠
		MahjongCheckResult res=new MahjongCheckResult();
		res.opertaion=0;
		res.targetCard =0;
		res.pengCardValue = 0;
		res.playerTableIndex = pl.getTablePos();

		//
		List<Byte> c1=pl.getCardsInHand();
		List<CardDown> c2=pl.getCardsDown();
		//
		int xiaojiNum = pl.getXiaojiNum();
		if(c1.size()>=minCardNum)
		{
			for(int k=0;k<c1.size();k++)
			{
				byte card = c1.get(k);
				//
				int num=pl.getXCardNumInHand(card);
				//
				if(k>0 && card==c1.get(k-1))
				{
					continue;//重复了
				}
				//
				if(num==4)
				{
					res.opertaion=GameConstant.MAHJONG_OPERTAION_AN_GANG;
					res.add_gang_card(card);
				}
				else
				{
					//小鸡不作为杠的提示牌
					if(card==0x11)
					{
						continue;
					}
					if(num==3 && xiaojiNum>0)
					{
						res.opertaion=GameConstant.MAHJONG_OPERTAION_AN_GANG;
						res.useXiaojiNum=1;
						res.add_gang_card(card);
						//xiaojiNum--;
						//res.useXiaojiNum=1;
					}/*
					 else
					{
						if(num==2 && xiaojiNum>1)
						{
							res.opertaion=GameConstant.MAHJONG_OPERTAION_AN_GANG;
							res.useXiaojiNum=2;
							res.add_gang_card(card);
							//xiaojiNum-=2;
						}

						else
						{
							if(num==1 && xiaojiNum>2)
							{
								res.opertaion=GameConstant.MAHJONG_OPERTAION_AN_GANG;
								res.useXiaojiNum=3;
								res.add_gang_card(card);
								//res.useXiaojiNum=3;
							}
						}
					} */
				}
			}
		}
		//补杠
		for (int i = 0; i < c2.size(); i++)
		{
			CardDown down = c2.get(i);
			int bb = down.cardValue;

			byte b1 = (byte) (bb & 0xff);

			if(down.type == GameConstant.MAHJONG_OPERTAION_PENG)
			{
				//
				int num=pl.getXCardNumInHand(b1);
				if(num>0 || xiaojiNum>0)
				{
					//补杠
					res.opertaion |=GameConstant.MAHJONG_OPERTAION_BU_GANG;
					res.add_gang_card(b1);
				}

			}
		}
		//
		if(res.has_gang())
			return res;
		//
		return null;
	}

	private MahjongCheckResult check_real_gang(MahjongCheckResult gang_result, Player plx, GameTable gt) {
		byte cardGrab = plx.getCardGrab();
		if (cardGrab != 0) {
			plx.addCardInHand(cardGrab);
			plx.setCardGrab((byte)0x0);
		}

		byte tailCard1 = (byte)0x25;
		byte tailCard2 = (byte)0x24;


		MahjongCheckResult hu = null;
		int gangNum = gang_result.gangList.size();
		if (gangNum >= 1) {
			if (gangNum >= 2) {
				if (gangNum == 2) {
					CardDown down1 = gang_result.gangList.get(0);
					byte gangCard1 = (byte)(down1.cardValue & 0xff);
					CardDown down2 = gang_result.gangList.get(1);
					byte gangCard2 = (byte)(down2.cardValue & 0xff);

					int num = plx.getXCardNumInHand(gangCard1);
					// 删除杠牌
					for (int i = 0; i < num; i++) {
						plx.removeCardInHand(gangCard1);
					}

					int num2 = plx.getXCardNumInHand(gangCard2);
					for (int i = 0; i < num2; i++) {
						plx.removeCardInHand(gangCard2);
					}

					plx.addCardInHand(tailCard2);
					plx.setCardGrab(tailCard1);
					hu = mahjongProcessor.checkWin(tailCard1, plx, gt);
					plx.removeCardInHand(tailCard2);

					// 恢复杠牌
					for (int i = 0; i < num; i++) {
						plx.addCardInHand(gangCard1);
					}
					for (int i = 0; i < num2; i++) {
						plx.addCardInHand(gangCard2);
					}

					// 两杠不能胡，检查单杠 gangCard1
					if (hu == null) {
						for (int i = 0; i < num; i++) {
							plx.removeCardInHand(gangCard1);
						}

						plx.setCardGrab(tailCard1);
						hu = mahjongProcessor.checkWin(tailCard1, plx, gt);

						if (hu == null) {
							plx.setCardGrab(tailCard2);
							hu = mahjongProcessor.checkWin(tailCard2, plx, gt);
						}

						if (hu != null) {
							gang_result.gangList.remove(1);
						}

						// 恢复杠牌
						for (int i = 0; i < num; i++) {
							plx.addCardInHand(gangCard1);
						}
					}

					// 两杠不能胡，检查单杠 gangCard2
					if (hu == null) {
						for (int i = 0; i < num2; i++) {
							plx.removeCardInHand(gangCard2);
						}

						plx.setCardGrab(tailCard1);
						hu = mahjongProcessor.checkWin(tailCard1, plx, gt);

						if (hu == null) {
							plx.setCardGrab(tailCard2);
							hu = mahjongProcessor.checkWin(tailCard2, plx, gt);
						}

						if (hu != null) {
							gang_result.gangList.remove(0);
						}

						// 恢复杠牌
						for (int i = 0; i < num2; i++) {
							plx.addCardInHand(gangCard2);
						}
					}
				}
			} else {
				CardDown down = gang_result.gangList.get(0);
				byte gangCard = (byte)(down.cardValue & 0xff);
				// 删除杠牌
				int num = plx.getXCardNumInHand(gangCard);
				for (int i = 0; i < num; i++) {
					plx.removeCardInHand(gangCard);
				}

				plx.setCardGrab(tailCard1);
				MahjongCheckResult gang = mahjongProcessor.checkGang(gt, tailCard1, plx, true);
				if (gang != null) {
					plx.setCardGrab(tailCard2);
					hu = mahjongProcessor.checkWin(tailCard2, plx, gt);
				}

				if (hu == null) {
					plx.setCardGrab(tailCard2);
					gang = mahjongProcessor.checkGang(gt, tailCard2, plx, true);
					if (gang != null) {
						plx.setCardGrab(tailCard1);
						hu = mahjongProcessor.checkWin(tailCard1, plx, gt);
					}
				}

				if (hu == null) {
					plx.setCardGrab(tailCard1);
					hu = mahjongProcessor.checkWin(tailCard1, plx, gt);
				}

				if (hu == null) {
					plx.setCardGrab(tailCard2);
					hu = mahjongProcessor.checkWin(tailCard2, plx, gt);
				}

				// 恢复杠牌
				for (int i = 0; i < num; i++) {
					plx.addCardInHand(gangCard);
				}
			}
			plx.removeCardInHand(cardGrab);
			plx.setCardGrab(cardGrab);
		}

		return hu;
	}

	private void death_machine(GameTable gt) {
		//System.out.print(".size:"+gt.getPlayers().size());
		for(Player pl:gt.getPlayers())
		{
			if(pl.isRobot())
				machineService.deathMachine(pl.getPlayerIndex());
		}
	}

	private void bk_player_bu_hua(GameTable gt,Player pl) {
		PlayerOperationNotifyMsg buHuaMsg=new PlayerOperationNotifyMsg();
		//
		int num=pl.removeHua(buHuaMsg);
		//
		if(num>0)
		{

			//
			for(int i=0;i<num;i++)
			{
				byte cd=gt.popCard();
				pl.addCardInHand(cd);
			}
			//
			//把所有牌都发给客户端，客户端刷新下手牌
			List<Byte>  c1=pl.getCardsInHand();
			for(int i=0;i<c1.size();i++){
				buHuaMsg.tingList.add(c1.get(i));
			}

			// 有补花,让服务器等客户端播下动画
			gt.setWaitingTianhuOrBuHua(true);
			//
			//buHuaMsg.operation=GameConstant.MAHJONG_OPERTAION_BU_HUA;
			buHuaMsg.setPlayerTablePos(pl.getTablePos());
			buHuaMsg.cardLeftNum=gt.getCardLeftNum();
			buHuaMsg.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), buHuaMsg);
		}
	}

	@Override
    protected void gameTableTick(GameTable gt) {
        Date ct=DateUtil.getCurrentUtilDate();
        long ctt=ct.getTime();
        int state=gt.getState();

        long vip_w_t=systemConfigService.getPara(GameConfigConstant.CONF_VIP_GAME_OVER_ENTER_NEXT_TURN).getValueInt() * 1000;
        long vip_wait_time=systemConfigService.getPara(GameConfigConstant.CONF_GAME_OVER_WAITING_START_GAME_TIME).getValueInt() * 1000;
//		if(gt.isVipTable() && gt.isVipTableTimeOver()){
//			vip_wait_time=3000;//如果是vip最后一把，不要等太长时间
//			vip_w_t=3000;
//		}
        //gt.printfPosition();

        //机器人桌，或者空桌回收
        if(gt.shouldRecycleTable())
        {
            gt.setState(GameConstant.TABLE_STATE_INVALID);
            return;
        }

        if (gt.getCloseVipRoomStartTime() > 0){//牌桌是解散中状态，检查是否要解散
            if(check_close_vip_room(gt)){
                return;
            }
        }

        if(state==GameConstant.GAME_TABLE_STATE_WAITING_PLAYER)
        {
            //桌子等待超过2秒
            long currentTime = DateUtil.getCurrentUtilDate().getTime();
            long delta = currentTime - gt.getVipCreateTime();
            if (!gt.isVipTable()) {
                // 当桌子为金币场,且桌子等待超过2秒时,则邀请机器人进入房间
                if (delta > 2000) {
                    this.inviteMachiner(gt);
                }
            } else {
                // vip房间如果超过一定时间没有开始游戏，房间结束
                if (delta > systemConfigService.getPara(GameConfigConstant.CONF_VIP_ROOM_CREATE_WAIT_START).getValueInt()
                        * 1000) {
                    // 通知玩家离开房间
                    PlayerOperationNotifyMsg msg = new PlayerOperationNotifyMsg();
                    msg.operation = GameConstant.MAHJONG_OPERTAION_NO_START_CLOSE_VIP;
                    this.sendMsgToTable(gt, msg);
                    gt.clearAll();
                    gt.setState(GameConstant.TABLE_STATE_INVALID);
                }
            }
        }
        else if(state==GameConstant.GAME_TABLE_STATE_READY_GO)
        {
            long delta=ctt-gt.getReadyTime();

            if(delta>2000)
            {
                gameStart(gt);
            }
        }
        else if(state==GameConstant.GAME_TABLE_WAITING_CLIENT_SHOW_INIT_CARDS)
        {
            long delta=ctt-gt.getReadyTime();
            if(delta> 3000)
            {

                if(delta> 3000)
                {
                    gt.setState(GameConstant.GAME_TABLE_STATE_PLAYING);
                    //提示庄家出牌
					player_chu_notify(gt,true,true);
                }

            }
        }
        else if(state==GameConstant.GAME_TABLE_STATE_PLAYING)
        {
            playingTableTick(gt,ctt);
        }
        else if(state==GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN)
        {
            long delta=ctt-gt.getHandEndTime();
            //游戏结束后2秒，给客户端播放排名
//			 int maNum=500*gt.getMaiMaNum();
            if(delta > vip_wait_time)
            {
                game_over(gt);
            }
        }
        else if(state==GameConstant.GAME_TABLE_STATE_PAUSED)
        {
            long delta=ctt-gt.getPausedTime();
            //游戏结束后120秒，给客户选择继续等待2分钟
            //*VIP场有玩家中途离场，玩家选择等待时间
            if(delta>=systemConfigService.getPara(GameConfigConstant.CONF_VIP_PAUSED_CHOOSE_WAIT_TIME).getValueInt() * 1000)
            {
                if(gt.isSendClientWaitingBreakPlayerNotify()==false)
                {
                    //
                    gt.setSendClientWaitingBreakPlayerNotify(true);
                    gt.clearCloseVipRoomMap();	//先清空同意结束房间人数

                    //发送消息给客户端，是否继续等待
                    PlayerOperationNotifyMsg msg=new PlayerOperationNotifyMsg();
                    msg.operation=GameConstant.MAHJONG_OPERTAION_WAITING_OR_CLOSE_VIP;
                    msg.setChiFlag(1);
                    this.sendMsgToTable(gt,msg,1);

                }
            }
            //Vip场有玩家中途离场，房间存活时间
            if(delta>systemConfigService.getPara(GameConfigConstant.CONF_VIP_PAUSED_EXIST_TIME).getValueInt() * 1000)//超时，关闭房间
            {
                GameRoom gr=this.getRoom(gt.getRoomID());

                logger.info("有玩家中途离场房间存活超时，VIP房间:" + gt.getVipTableID() +"结束");

                vip_table_end(gr, gt);
                gt.clearAll();
                gt.setState(GameConstant.TABLE_STATE_INVALID);
            }

            //VIP圈数用完，等待续卡时间
            if(delta>=systemConfigService.getPara(GameConfigConstant.CONF_GAME_OVER_WAITING_START_GAME_TIME).getValueInt() * 1000)
            {
                if(gt.isVipTable() && gt.isVipTableTimeOver() && gt.getBackupState() == GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE )
                {
                    logger.info("VIP圈数用完，等待续卡时间超过，VIP房间" + gt.getVipTableID() + "结束");

                    //不续卡，或者房卡不足，直接结束房间
                    GameRoom gr=this.getRoom(gt.getRoomID());
                    vip_table_end(gr,gt);
                }
            }


            //看看桌子上玩家是否都已经到齐
            if(gt.pauseContinue())
            {
                if(gt.getState()==GameConstant.GAME_TABLE_STATE_PLAYING)
                {
                    //断线回来，如果当前桌子子状态为0，说明没有等待状态，直接提示当前玩家行动
                    if(gt.getPlaySubstate()==0)//
                    {
                        //通知玩家行动
                        re_notify_current_operation_player(gt);
                    }else
                    {
                        //否则就把等待时间设置为当前，系统等待一定时间后，提示玩家进行应该进行的动作，比如有玩家吃碰等
                        Date dt=DateUtil.getCurrentUtilDate();
                        gt.setWaitingStartTime(dt.getTime());
                    }
                }
            }
        }
        else if(state==GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE)
        {
            if(gt.getPlayerNum()<gt.getMaxPlayer() && gt.getWaitingStartTime()!=0&&ctt-gt.getWaitingStartTime()>2000)
            {
                inviteMachiner(gt);
            }

            //
            long delta=ctt-gt.getHandEndTime();

            //人满随时开始，如果大家都点击了继续
            if(gt.getReadyPlayerNum()==gt.getMaxPlayer())
            {
                if(gt.isVipTable() && gt.isVipTableTimeOver())
                {
                    //vip圈数用光，继续等待,超时就结束
                    //vip圈数结束，继续等待时间
                    if(delta >= vip_wait_time)
                    {
                        GameRoom gr=getRoom(gt.getRoomID());

                        logger.info("vip圈数用光超时结束，VIP房间:" + gt.getVipTableID() +"结束");

                        vip_table_end(gr,gt);
                    }
                    return;
                }
                else
                {
                    //把游戏数据先清理掉，保留玩家
                    gt.clearGameState();
                    //人满开始下一局
                    gt.setReadyTime(ctt);
                    gt.setState(GameConstant.GAME_TABLE_STATE_READY_GO);
                }
            }
            else if(delta>=vip_w_t && gt.isVipTable())//vip牌局结束15秒，自动进下一把
            {
//				 System.out.println("VIP房间结算等待时间超过" + delta + "自动开始下一局");

                if(gt.isVipTableTimeOver())
                {
                    GameRoom gr=getRoom(gt.getRoomID());

                    logger.info("vip圈数用光，VIP房间:" + gt.getVipTableID() +"结束");

                    vip_table_end(gr,gt);
                }
                else
                {
//					 if(vip_table_check_next_hand(gt))
//					 {
//						 vip_next_hand(gt,ctt);
//					 }
                }
            }
            //游戏结束后60秒，等60秒继续
            else if(delta>=30*1000)
            {
                if(!gt.isVipTable())
                {
                    System.out.println("房间结算等待时间超过" + delta + "房间结束");

                    //把未准备好的玩家删掉
                    //gt.set_not_ready_player_ready();
                    gt.remove_not_ready_player();
                    if(gt.getPlayerNum()==gt.getMaxPlayer())
                    {
                        next_hand(gt,ctt);
                    }
                    else if(gt.getReadyPlayerNum()> 0)
                    {
                        //把游戏数据先清理掉，保留玩家
                        gt.clearGameState();
                        //超过满开始下一局
                        gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PLAYER);
                        //重新邀请机器人
                        machineService.reset_machinetime_current(gt);
                    }else
                    {
                        //如果没有人玩，状态非法，后面的逻辑收掉这个桌子
                        gt.clearAll();
                    }
                }
            }
        }
    }

	private void vip_next_hand(GameTable gt,long ctt)
	{
		next_hand(gt,ctt);
	}

	public void next_hand(GameTable gt,long ctt) {
		List<Player>  plist=gt.getPlayers();

		//
		for(int i=0;i<plist.size();i++)
		{
			Player pl=plist.get(i);
			if(pl.getGameState()==GameConstant.PALYER_GAME_STATE_IN_TABLE_READY)
				continue;
			//
			pl.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_READY);
			//
			RequestStartGameMsgAck ack=new RequestStartGameMsgAck();

			ack.setResult(ConstantCode.CMD_EXE_OK);
			ack.setTotalHand(gt.getHandsTotal());
			ack.setCurrentHand(gt.getHandNum());

			//
			if(gt.isVipTable())
			{
				ack.initPlayers(gt.getPlayers(),true);
				ack.setGold(pl.getVipTableGold());
			}
			else
			{
				ack.init_players_time_hand(gt.getPlayers());
				ack.setGold(pl.getGold());
			}
			//
			ack.setRoomID(gt.getRoomID());
			ack.setTablePos(pl.getTablePos());

			ack.setVipTableID(gt.getVipTableID());
			ack.setCreatorName(gt.getCreatorPlayerName());
            ack.setCreatePlayerID(Integer.toString(gt.getCreatorPlayerIndex()));
			ack.setNewPlayWay(gt.getVipRule());
			//
			//进入桌子，返回消息给玩家
			ack.sessionID=pl.getPlayerLobbySessionID();
			SystemConfig.gameSocketServer.sendMsg(pl.getSession(), ack);


			//给桌子上的其他玩家发个进入新玩家的消息
			PlayerGameOpertaionAckMsg axk=new PlayerGameOpertaionAckMsg();
			axk.opertaionID=GameConstant.GAME_OPERTAION_TABLE_ADD_NEW_PLAYER;
			axk.playerName=pl.getPlayerName();
			axk.targetPlayerName=pl.getHeadImageUrl();
			axk.gold=pl.getVipTableGold();
			axk.headImg=pl.getHeadImg();
			axk.sex=pl.getSex();
			axk.tablePos=pl.getTablePos();
			axk.playerID=pl.getPlayerID();
			axk.playerIndex=pl.getPlayerIndex();
			axk.canFriend = pl.getCanFriend();
			axk.ip = pl.getClientIP();
			axk.sessionID=pl.getPlayerLobbySessionID();
			this.sendMsgToTableExceptMe(gt,axk,pl.getPlayerID());

		}

		//把游戏数据先清理掉，保留玩家
		gt.clearGameState();
		//人满开始下一局
		gt.setReadyTime(ctt);
		gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_READY);
		gt.setState(GameConstant.GAME_TABLE_STATE_READY_GO);
	}

	// vip锅结束
	public void vip_table_end(GameRoom gr, GameTable gt) {
		vip_table_end(gr, gt, null);
	}

	//plx为空，服务器结束；plx不为空，客户端请求结束
    public void vip_table_end(GameRoom gr, GameTable gt, Player plx) {
        logger.info("VIP锅结束了, tableID=" + gt.getTableID());

        List<Player>  plist=gt.getPlayers();
        if( !gt.isHascopyplayers() )
        {
            for(Player pltemp:plist)
            {
                Player pl_bakPlayer = new Player();
                //pl_bakPlayer.copy_game_state(pl);
                try {
                    CommonUtils.copyPropertiesExtNull(pl_bakPlayer, pltemp);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                gt.getPlayers_bak().add(pl_bakPlayer);
            }

            gt.setHascopyplayers(true);
        }


        ////记录胜负平日志
        for(int i=0;i<plist.size();i++)
        {
            Player px=plist.get(i);

            int total_gold=px.getVipTableGold();
            if(total_gold>0)
                px.setWons(px.getWons()+1);
            else if(total_gold<0)
                px.setLoses(px.getLoses()+1);
            else
                px.setEscape(px.getEscape()+1);
            //
            this.playerService.updatePlayerRecord(px);
        }

        //
        VipRoomCloseMsg closeMsg=new VipRoomCloseMsg();
//		closeMsg.unused_0=gt.getVipTableID();//房号返回
//		closeMsg.unused_1 = gt.getCreator().getTablePos(); // 房主标记
        //

//        closeMsg.init_players(gt.getPlayers(), gt,gr.getFixedGold());
        closeMsg.init_players(gt.getPlayers_bak(), gt,gr.getFixedGold());

        if (plx != null) {
            logger.info("VIP锅结束了, tableID=" + gt.getTableID() + " playerName=" + plx.getPlayerName());
            closeMsg.sessionID = plx.getPlayerLobbySessionID();
            GameSocketServer.sendMsg(plx.getSession(), closeMsg);
        } else {
            this.sendMsgToTable(gt, closeMsg);
        }

        boolean clearTable = false;
        if (plx != null) {
            plx.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_PAUSED);
            if (gt.isAllPlayerLeft()) {
                clearTable = true;
            }

            // 清理掉玩家的桌子vip金币
            plx.setVipTableGold(0);
            plx.setHuFanNum(0);
            // vip开房打完，把金币加上金币账户
            if (plx.getWinLoseTotal() > 0) {
                playerService.add_player_gold(plx, plx.getWinLoseTotal(),
                        LogConstant.OPERATION_TYPE_ADD_GOLD_GAME_WIN,
                        "vip开房结束金币加上，gold=" + plx.getWinLoseTotal());
            }
            // 输赢金币清零
            plx.setWinLoseTotal(0);
            // 清除VIP桌子统计信息
            plx.clearVipTableInfo();
            plx.setVipTableID("");

            gt.getPlayers().remove(plx);

        } else {
            clearTable = true;
        }

        if (clearTable) {
            for (int i = 0; i < plist.size(); i++) {
                Player pl = plist.get(i);
                // 清理掉玩家的桌子vip金币
                pl.setVipTableGold(0);
                pl.setHuFanNum(0);
                // vip开房打完，把金币加上金币账户
                if (pl.getWinLoseTotal() > 0) {
                    playerService.add_player_gold(pl, pl.getWinLoseTotal(),
                            LogConstant.OPERATION_TYPE_ADD_GOLD_GAME_WIN,
                            "vip开房结束金币加上，gold=" + pl.getWinLoseTotal());
                }
                // 输赢金币清零
                pl.setWinLoseTotal(0);
                // 清除VIP桌子统计信息
                pl.clearVipTableInfo();
                pl.setVipTableID("");
            }

            gt.clearAll();
            gt.setState(GameConstant.TABLE_STATE_INVALID);
        }
    }

	//
	//检测是否玩家输光，第一局玩家扣金币
	private boolean vip_table_check_next_hand(GameTable gt) {
		if(gt.isVipTable()==false)
			return false;

		if( gt.getHandNum()==0)
			return true;

		GameRoom gr=getRoom(gt.getRoomID());
		if(gr==null){
			gt.setState(GameConstant.TABLE_STATE_INVALID);
			return false;
		}
		//先测试金币够不够
//		for(int i=0;i<plist.size();i++)
//		{
//			 Player pl=plist.get(i);
//			 if(pl.getVipTableGold()<gr.minGold)//vip 桌子当玩家金币小于minGold就解散
//			 {
//				 gt.setState(GameConstant.TABLE_STATE_INVALID);
//
//				 logger.info("玩家金币不足，VIP房间:" + gt.getVipTableID() +"结束");
//
//				 vip_table_end(gr,gt);
//				 return false;
//			 }
//		}
		//
		if(gt.isVipTableTimeOver())
			return false;

		return true;
	}
	//
	private void game_over_log(GameTable gt) {

		//写日志
		List<Player>  plist=gt.getPlayers();
		if(gt.isVipTable())
		{
			//
			for(int i=0;i<plist.size();i++)
			{
				Player pl=plist.get(i);
				//
				VipRoomRecord vrr=pl.getVrr();
				if(vrr!=null)
				{
					vrr.setRoomType(pl.getRoomID());

					int num=0;
					for(int j=0;j<plist.size();j++)
					{
						Player plj=plist.get(j);
						if(plj.getPlayerID().equals(pl.getPlayerID())==false)
						{
							if(num==0){
								vrr.setPlayer2Name(plj.getPlayerIndex() + "-" +plj.getPlayerNameNoEmoji());
								vrr.setScore2(plj.getWinLoseTotal());
							}
							else if(num==1){
								vrr.setPlayer3Name(plj.getPlayerIndex() + "-" +plj.getPlayerNameNoEmoji());
								vrr.setScore3(plj.getWinLoseTotal());
							}
							else if(num==2){
								vrr.setPlayer4Name(plj.getPlayerIndex() + "-" +plj.getPlayerNameNoEmoji());
								vrr.setScore4(plj.getWinLoseTotal());
							}
							//
							num++;
						}else
						{
							vrr.setScore1(pl.getWinLoseTotal());
						}
					}

					//更新结束时间
					vrr.setEndTime(new Date());

					playerService.updateVipRoomRecord(vrr);
				}


				//
				VipGameRecord vgr=new VipGameRecord();
				vgr.setRoomType(pl.getRoomID());
				vgr.setPlayerID(pl.getPlayerID());
				vgr.setPlayerName(pl.getPlayerIndex() + "-" +pl.getPlayerNameNoEmoji());
				vgr.setRoomID(gt.getTableID());
				//
				vgr.setGameID(gt.getVipTableID());
				//
				vgr.setGameScore(pl.getWinLoseGoldNum()+pl.getGangMoneyNum());
				vgr.setGameType1(pl.getFanType());
				vgr.setRecordDate(DateUtil.getCurrentUtilDate());
				vgr.setHandIndex(gt.getHandNum());
				vgr.setGameMultiple(pl.getFanNum());

				//
				this.playerService.createVipGameRecord(vgr);
			}
		}else
		{
			Player px0=null;
			Player px1=null;
			Player px2=null;
			Player px3=null;
			if(plist.size()>0)
				px0=plist.get(0);
			if(plist.size()>1)
				px1=plist.get(1);
			if(plist.size()>2)
				px2=plist.get(2);
			if(plist.size()>3)
				px3=plist.get(3);


			NormalGameRecord ngr=new NormalGameRecord();
			ngr.setRoomID(gt.getTableID());
			ngr.setRoomType(gt.getRoomID());
			//
			if(px0!=null){
				ngr.setPlayer1Index(px0.getPlayerIndex());
				ngr.setGameType1(px0.getFanType());
				ngr.setScore1(px0.getWinLoseGoldNum()+px0.getGangMoneyNum());
				ngr.setGameMultiple1(px0.getFanNum());
			}

			//
			if(px1!=null){
				ngr.setPlayer2Index(px1.getPlayerIndex());
				ngr.setGameType2(px1.getFanType());
				ngr.setScore2(px1.getWinLoseGoldNum()+px1.getGangMoneyNum());
				ngr.setGameMultiple2(px1.getFanNum());
			}

			//
			if(px2!=null){
				ngr.setPlayer3Index(px2.getPlayerIndex());
				ngr.setGameType3(px2.getFanType());

				ngr.setScore3(px2.getWinLoseGoldNum()+px2.getGangMoneyNum());
				ngr.setGameMultiple3(px2.getFanNum());
			}

			//
			if(px3!=null){
				ngr.setPlayer4Index(px3.getPlayerIndex());
				ngr.setGameType4(px3.getFanType());

				ngr.setScore4(px3.getWinLoseGoldNum()+px3.getGangMoneyNum());
				ngr.setGameMultiple4(px3.getFanNum());
			}

			ngr.setHostIndex(px0.getPlayerIndex());
			Date tt=new Date(gt.getHandStartTime());
			ngr.setStartTime(tt);
			ngr.setEndTime(DateUtil.getCurrentUtilDate());
			//
			this.playerService.createNormalGameRecord(ngr);

		}

	}

	private void processGameOver(GameTable gt){
		// 第一局结束扣房卡
		if (gt.isVipTable()) {
			if (!gt.isPayVipKa()) {
				if (!playerService.use_fangka(gt)) {
					cleanTable(gt);
					return;
				}
				gt.setPayVipKa(true);
			}
		}
		//牌局结束给每个人发个全量
		List<Player> plist=gt.getPlayers();
		for(int i=0;i<plist.size();i++)
		{
			Player p=plist.get(i);
			if (p != null && p.isNewVersion()){
				if(gt.isFull()){
					enterTableDone(p,gt,true,true);
				}else{
					enterTableDone(p,gt,false,true);
				}
			}
		}

		//发送游戏结束界面
		PlayerGameOverMsgAck gov=new PlayerGameOverMsgAck();
		gov.roomID=gt.getRoomID();
		gov.huCard=gt.getHu_card()&0xff;
		gov.dealerPos=gt.getDealerPos();
		gov.huPos = gt.getCurrentHuPlayerIndex();
		gov.baoCard = gt.getRiffraffCard();
		int b2=gt.getSecondBaoCard();
		int b3=gt.getLazarailloCard();
		int b4=gt.getSecondBaoCard2();
		gov.baoCard|=(b2<<8);
		gov.baoCard|=(b3<<16);
		gov.baoCard|=(b4<<24);
		//
		gov.init_players(gt.getPlayers(), gt);
		//
		if (gt.isVipTable())
		{
			gov.isVipTable=1;
			gov.readyTime=systemConfigService.getPara(GameConfigConstant.CONF_VIP_GAME_OVER_ENTER_NEXT_TURN).getValueInt();
		}
		else
		{
			gov.isVipTable=0;
			gov.readyTime=30;
		}
		this.sendMsgToTable(gt,gov,1);
//		 this.resetTableSeq(gt);
		//

		//vip输赢累加
//		 List<Player>  plist=gt.getPlayers();
		if(gt.isVipTable())
		{
			for(int i=0;i<plist.size();i++)
			{
				Player pl=plist.get(i);
				//带正负号
				pl.setWinLoseTotal(pl.getWinLoseTotal() + pl.getWinLoseGoldNum());
			}
		}
		//日志处理
		game_over_log(gt);

		/***********现在不需要续卡**********/
		//VIP最后一把，圈数用光
//		 if(gt.isVipTableTimeOver())
//		 {
//			 extend_card_remind(gt);
//			 //给其他玩家发个消息，等待房主续卡
//			PlayerOperationNotifyMsg mxg=new PlayerOperationNotifyMsg();
//			mxg.operation=GameConstant.MAHJONG_OPERTAION_EXTEND_CARD_REMIND;
//			mxg.target_card=1;//不是房主才处理这个消息
//
//			Player pl=gt.getCreator();
//			if(pl!=null)
//			{
//				this.sendMsgToTableExceptMe(gt,mxg, pl.getPlayerID());
//			}
//		 }
		gt.clearGameState();
		gt.setGangOpBackup(null);
		gt.setGangMsgBackup(null);
		gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
		//然后，游戏进入等待玩家继续界面
		gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);


		GameRoom gr = gameRoomMap.get(gt.getRoomID());


		//机器人处理
		if(!gt.isVipTable()){
			for(int i=0;i<plist.size();i++)
			{
				Player pl=plist.get(i);

				if(!pl.getOnTable()){
					playerLeftTable(pl);
				}
				else if(pl.getGold()<gr.getMinGold()){
					playerLeftTable(pl);
				}
				else if(pl.isRobot()){
					pl.dcRobotgamenum();
					if(pl.getRobotgamenum()<=0){
						playerLeftTable(pl);
					}
				}
			}
		}


		gt.setAllRobotReady();
		gt.setHandEndTime(System.currentTimeMillis());
		machineService.reset_machinetime_current(gt);
	}

	// vip圈数用光，用户请求结束
	public void game_over(Player pl, GameTable gt) {
		if (gt.isVipTable() && gt.isVipTableTimeOver()) {
			GameRoom gr = getRoom(gt.getRoomID());

			logger.info("vip圈数用光，player=" + pl.getPlayerName() + "结束，VIP房间:" + gt.getVipTableID()
					+ "结束");

			vip_table_end(gr, gt, pl);
		} else {
			logger.info("player=" + pl.getPlayerName() + " 请求结束错误");
		}
	}

    private void inviteMachiner(GameTable gameTable) {
        // VIP房间只有正式玩家才能玩,不会邀请机器人进入
        boolean result = (!gameTable.isVipTable()) && (gameTable.getPlayerNum() > 0) && !gameTable.isFull();
        if (result) {
            GameRoom gameRoom = gameRoomMap.get(gameTable.getRoomID());
            long currentTime = DateUtil.getCurrentUtilDate().getTime();
            for (Player player : gameTable.getPlayers()) {
                // 如果有整个桌子等待玩家超过2秒并且有玩家等待超过5秒，立即邀请机器人
                if (player.isRobot() || player.getInRoomWaitOtherTime() == 0) {
                    continue;
                }
                int waitLimitTime = systemConfigService.getRobotWaitLimitTime() * 1000;
                long waitingTime = currentTime - player.getInRoomWaitOtherTime();
                if (waitingTime > waitLimitTime) {
                    List<Player> robots = machineService.createMachine(gameRoom, gameTable);
                    if (robots != null && !robots.isEmpty()) {
                        gameTable.setLastEnteringTime(0);
                        for (Player robot : robots) {
                            // 机器人进入金币场桌子
                            this.tryEnterRoom(robot, robot.getRoomID());
                        }
                    }
                    break;
                }
            }
        }
    }

	//
	public ISystemConfigService getCfgService() {
		return systemConfigService;
	}

	public void setCfgService(ISystemConfigService cfgService) {
		this.systemConfigService = cfgService;
	}

	public MahjongProcessor getDaqingMahjong()
	{
		return mahjongProcessor;
	}

	public IMachineService getMachineService() {
		return machineService;
	}

	public IPlayerService getPlayerService() {
		return playerService;
	}

	/**强制结束游戏，用于从后台结束非法桌子*/
	public void game_over_force(Player pl) {
		logger.info("强制清理玩家信息，结束非法桌子");

		int roomID=pl.getRoomID();
		GameRoom rm=gameRoomMap.get(roomID);
		if(rm==null)
		{
			logger.info("找不到房间，不需要结束");
			return;
		}

		GameTable gt=rm.getTable(pl.getTableID());
		if(gt!=null)
		{
			logger.error("房间ID:" + roomID + " 桌子ID：" + pl.getTableID() + " 桌子状态State：" + gt.getState() + " 当前人数：" + gt.getPlayerNum());
			logger.error("本局开始时间：" + new Date(gt.getHandStartTime())  + " 当前操作人currentOpertaionPlayerIndex:" + gt.getCurrentOpertaionPlayerIndex());
			for(Player ply:gt.getPlayers())
			{
				if (null != ply)
				{
					String cards="";
					for(Byte card:ply.getCardsInHand())
					{
						cards += card;
						cards += "-";
					}
					logger.error("玩家"+ ply.getPlayerIndex() + " 座位号：" + ply.getTablePos() + " 手牌：" + cards);
				}
			}

			gt.clearGameState();
			gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);
			gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
			gt.setHandEndTime(System.currentTimeMillis());

			//设为非法状态，系统会自动清理
			gt.setState(GameConstant.TABLE_STATE_INVALID);
		}
		else
		{
			logger.info("找不到桌子，不需要结束");
		}
	}


	public GameTable getVipTableByVipTableID(int vipTableID) {
		if(vipTableID==0)
			return null;

		for(Integer rid:gameRoomMap.keySet())
		{
			GameRoom gr=gameRoomMap.get(rid);

			GameTable gt=gr.getVipTableByVipTableID(vipTableID)	;
			if(gt!=null)
			{
				return gt;
			}
		}
		return null;
	}

	public void force_clear_table_by_admin(int vipTableID) {
		if(vipTableID==0)
			return;

		for(Integer rid:gameRoomMap.keySet())
		{
			GameRoom gr=gameRoomMap.get(rid);

			GameTable gt=gr.getVipTableByVipTableID(vipTableID)	;
			if(gt!=null)
			{
				clear_game_table_by_admin(gt);
				return;
			}
		}
	}
	/**强制结束游戏，用于从后台结束非法桌子*/
	public void game_over_force_one_table(String table_id) {
		logger.info("==强制清理单个桌子==");

		GameTable gt = null;
		for(Integer rid:gameRoomMap.keySet())
		{
			GameRoom gr=gameRoomMap.get(rid);

			Map<String, GameTable> playingTables = gr.getPlayingTablesMap();

			gt = playingTables.get(table_id);
			if(gt!=null)
				break;
		}

		if(gt!=null)
		{
			clear_game_table_by_admin(gt);
		}
		else
		{
			logger.info("找不到桌子，不需要结束");
		}
	}

	private void clear_game_table_by_admin(GameTable gt) {
		logger.error("vip房间ID:" + gt.getVipTableID() + " 桌子ID：" + gt.getTableID() + " State：" + gt.getState() + " Backup_state:" + gt.getBackupState() + " 当前人数：" + gt.getPlayerNum());
		logger.error("本局开始时间：" + new Date(gt.getHandStartTime())  + " 当前操作人currentOpertaionPlayerIndex:" + gt.getCurrentOpertaionPlayerIndex());
		for(Player ply:gt.getPlayers())
		{
			if (null != ply)
			{
				String cards="";
				for(Byte card:ply.getCardsInHand())
				{
					cards += card;
					cards += "-";
				}
				logger.error("玩家"+ ply.getPlayerIndex() + "座位号：" + ply.getTablePos() + ",玩家状态：" + ply.getGameState() + ",手牌：" + cards);
			}
		}

		gt.clearGameState();
		gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);
		gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
		gt.setHandEndTime(System.currentTimeMillis());

		//设为非法状态，系统会自动清理
		gt.setState(GameConstant.TABLE_STATE_INVALID);
	}
	/**游戏中玩家发送快捷聊天语句*/
	public void playerTalkingInGame(Player pl, TalkingInGameMsg msg) {
		if(pl==null)
			return;

		GameRoom gr=this.getRoom(pl.getRoomID());
		if(gr==null)
			return;

		GameTable gt=gr.getTable(pl.getTableID());
		if(gt==null)
			return;

		msg.playerSex = pl.getSex();
		msg.playerPos=pl.getTablePos();

		if(msg.msgType==3)
			this.sendMsgToTableExceptMe(gt, msg, pl.getPlayerID());
		else
			//转发给桌子上其他人
			this.sendMsgToTable(gt,msg);
	}

	/**获取正在进行游戏的桌子*/
	public List<GameTable> getPlayingTables(int index) {
		List<GameTable> tables = new ArrayList<GameTable>();

		for(Integer rid:gameRoomMap.keySet())
		{
			GameRoom gr=gameRoomMap.get(rid);

			Map<String, GameTable> playingTables = gr.getPlayingTablesMap();

			for(String tid:playingTables.keySet())
			{

				if(index > 0)
				{
					if(playingTables.get(tid).isHavePlayer(index))
					{
						tables.add(playingTables.get(tid));
					}
				}
				else
				{
					tables.add(playingTables.get(tid));
				}
			}
		}

		return tables;
	}

	/**
	 * 请求游戏开始
	 *
	 * @param msg
	 * @param session
	 */
	@Override
	public void requestStartGame(RequestStartGameMsg msg, IoSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("requestStartGame,roomId:{}", msg.getRoomID());
		}
		logger.info("CHAOS_ZHANG requestStartGame");
		XiantaoPlayer player = (XiantaoPlayer) playerService.getPlayerBySessionID(msg.sessionID);
		if (player == null) {
			logger.error("玩家不存在,request start game fail CHAOS_ZHANG");
			return;
		}
		this.tryEnterRoom(player, msg.roomID);
	}

    protected void enterTableDone(Player player, GameTable gameTable, boolean isReenter, boolean is_board_quanliang) {
        player.setOnTable(true);
        player.setAutoOperation(0);
        RequestStartGameMsgAck ack = new RequestStartGameMsgAck();
        ack.setTotalHand(gameTable.getHandsTotal());
        ack.setCurrentHand(gameTable.getHandNum());

        if (gameTable.getState() == GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE) {
            player.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_READY);
        } else if (gameTable.getState() == GameConstant.GAME_TABLE_STATE_PAUSED) {
            if (gameTable.getBackupState() == GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE) {
                player.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_READY);
            } else {
                player.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_PLAYING);
            }
        }
        ack.setResult(ConstantCode.CMD_EXE_OK);
        ack.initPlayers(gameTable.getPlayers(), gameTable.isVipTable());
        ack.setGold(player.getGold());
        ack.setRoomID(player.getRoomID());
        ack.setTablePos(player.getTablePos());
        ack.setNewPlayWay(gameTable.getVipRule());
        GameRoom gameRoom = this.getRoom(gameTable.getRoomID());
        if ((gameRoom != null) && (gameRoom.getRoomType() == GameConstant.ROOM_TYPE_VIP)) {
            ack.setVipTableID(gameTable.getVipTableID());
        }
        ack.setCreatorName(gameTable.getCreatorPlayerName());
        ack.setCreatePlayerID(Integer.toString(gameTable.getCreatorPlayerIndex()));
        ack.setSessionID(player.getPlayerLobbySessionID());
        GameSocketServer.sendMsg(player.getSession(), ack);// 进入桌子，返回消息给玩家

        this.refreshClientSeq(gameTable, player, 0);
        logger.info("玩家进入桌子完毕, uid:{},tableId:{}", player.getPlayerIndex(), player.getTableID());

        // 断线返回，VIP房间在结算的时候断线回来要走下面的分支
        if (isReenter && (gameTable.getState() != GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE)) {
            this.reenterTable(player, gameTable, is_board_quanliang);
        } else {
            // 通知桌子上的其他玩家进入桌子
            PlayerGameOpertaionAckMsg axk = new PlayerGameOpertaionAckMsg();
            axk.setOpertaionID(GameConstant.GAME_OPERTAION_TABLE_ADD_NEW_PLAYER);
            axk.setPlayerName(player.getPlayerName());
            axk.setTargetPlayerName(player.getHeadImageUrl());
            if (gameTable.isVipTable()) {
                axk.setGold(player.getVipTableGold());
            } else {
                axk.setGold(player.getGold());
            }
            axk.setHeadImg(player.getHeadImg());
            axk.setSex(player.getSex());
            axk.setTablePos(player.getTablePos());
            axk.setPlayerID(player.getPlayerID());
            axk.setPlayerIndex(player.getPlayerIndex());
            axk.setCanFriend(player.getCanFriend());
            axk.setIp(player.getClientIP());
            axk.setSessionID(player.getPlayerLobbySessionID());
            this.sendMsgToTableExceptMe(gameTable, axk, player.getPlayerID());

            //若用户在打完牌局，还未推出table时，玩家掉线，此时应该把战绩发给用户
            if(gameTable.isVipTableTimeOver() && gameTable.isVipTable())
            {
                vip_table_end(gameRoom, gameTable, player);
            }
        }
    }

    /**
     * 超时处理，打第一张牌
     *
     * @param player
     * @param gameTable
     */
    //uncertainty
    protected void processOverTime(Player player, GameTable gameTable) {
//		if(pl.isRobot())
//			return;

        if(gameTable.getState()==GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE
                ||gameTable.getState()==GameConstant.GAME_TABLE_STATE_WAITING_PLAYER)
        {
            player.setAutoOperation(0);
            gameTable.setWaitingPlayerOperate(null);
            return;
        }

        //
        PlayerTableOperationMsg msg=new PlayerTableOperationMsg();
        //
        MahjongCheckResult wt=gameTable.getWaitingPlayerOperate();
        //自动吃碰听
        if(wt!=null)
        {
            //所有操作全部按过处理
            //所有操作全部按过处理
            if(((wt.opertaion&GameConstant.MAHJONG_OPERTAION_CHI)==GameConstant.MAHJONG_OPERTAION_CHI)
                    ||((wt.opertaion&GameConstant.MAHJONG_OPERTAION_PENG)==GameConstant.MAHJONG_OPERTAION_PENG)
                    ||((wt.opertaion&GameConstant.MAHJONG_OPERTAION_TING)==GameConstant.MAHJONG_OPERTAION_TING)
                    ||((wt.opertaion&GameConstant.MAHJONG_OPERTAION_GANG)==GameConstant.MAHJONG_OPERTAION_GANG)
                    ||((wt.opertaion&GameConstant.MAHJONG_OPERTAION_HU)==GameConstant.MAHJONG_OPERTAION_HU))
            {
                msg.setPlayerTablePos(wt.playerTableIndex);
                msg.operation=GameConstant.MAHJONG_OPERTAION_CANCEL;

                //通知客户端隐藏吃、碰、听提示框
                PlayerOperationNotifyMsg CancelMsg=new PlayerOperationNotifyMsg();
                CancelMsg.operation=GameConstant.MAHJONG_OPERTAION_CANCEL;
                CancelMsg.setPlayerTablePos(player.getTablePos());
                CancelMsg.cardLeftNum=gameTable.getCardLeftNum();
                CancelMsg.sessionID=player.getPlayerLobbySessionID();
                SystemConfig.gameSocketServer.sendMsg(player.getSession(), CancelMsg);

            }
            else if((wt.opertaion&GameConstant.MAHJONG_OPERTAION_POP_LAST)==GameConstant.MAHJONG_OPERTAION_POP_LAST){
                msg.operation = GameConstant.MAHJONG_OPERTAION_POP_LAST;
                msg.setCardValue(1);
            }
        }

        //
        if(msg.operation==0)
        {
            //自动出牌
            msg.operation=GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU;
            msg.setPlayerTablePos(player.getTablePos());
            //
            if(msg.getCardValue()==0)
            {
                msg.setCardValue(getAutoChuCard(player));
            }


            //自己是自动出牌
            PlayerOperationNotifyMsg autoMsg=new PlayerOperationNotifyMsg();
            autoMsg.operation=GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU;
            autoMsg.setPlayerTablePos(player.getTablePos());
            autoMsg.target_card=msg.getCardValue();
            autoMsg.cardLeftNum=gameTable.getCardLeftNum();
            autoMsg.sessionID=player.getPlayerLobbySessionID();
//			gt.cachePlayerMsg(pl.getPlayerID(), autoMsg);
//			int seq = gt.addSeq(pl.getPlayerID());
//			autoMsg.unused_2 = seq;

            //SystemConfig.gameSocketServer.sendMsg(pl.getSession(), autoMsg);
            SystemConfig.gameSocketServer.sendMsgWithSeq(player.getSession(), autoMsg, player.getPlayerID(), gameTable);
        }


        //设置成托管状态
        player.setAutoOperation(1);

        if(msg.operation== GameConstant.MAHJONG_OPERTAION_CANCEL)
        {
            //给PL发超时的消息，自动进入托管
            PlayerOperationNotifyMsg autoMsg=new PlayerOperationNotifyMsg();
            autoMsg.operation=GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU;
            autoMsg.setPlayerTablePos(player.getTablePos());
            autoMsg.target_card=msg.getCardValue();
            autoMsg.cardLeftNum=gameTable.getCardLeftNum();
            autoMsg.sessionID=player.getPlayerLobbySessionID();

            SystemConfig.gameSocketServer.sendMsgWithSeq(player.getSession(), autoMsg, player.getPlayerID(), gameTable);
        }
		//uncertainty
        playerOperation(msg,player);
        //pl.setGameState(GameConstant.PALYER_GAME_STATE_IN_TABLE_PLAYING);
        //
    }

    private boolean checkPlayerTing(Player pl,GameTable gt) {
        //
        MahjongCheckResult ting=mahjongProcessor.check_ting((byte)0x0, pl, gt);
        if(ting!=null)
        {
            //
            pl.setFanType(pl.getFanType()|ting.fanResult);
            return true;
        }
        return false;
    }

    public void calculate_player_max_fan(GameTable gt,Player pl) {
        pl.setMaxFanNum(0);
        List<Byte>cds=new ArrayList<Byte>();
        List<Byte>cds_inhand=pl.getCardsInHand();

        XiantaoMahjongProcessor xiantaoMahjongProcessor =
                (XiantaoMahjongProcessor)mahjongProcessor;

        for(int i=0;i<cds_inhand.size();i++)
        {
            int b=cds_inhand.get(i);


            //
            xiantaoMahjongProcessor.add_card_unique(cds,b);
            xiantaoMahjongProcessor.add_card_unique(cds,b-1);
            xiantaoMahjongProcessor.add_card_unique(cds,b+1);
        }
        String hudesc="";
        for(int i=0;i<cds.size();i++)
        {
            byte b=cds.get(i);
            MahjongCheckResult hu= xiantaoMahjongProcessor.checkWin(b, pl, gt);
            if(hu!=null)
            {
                hu.fanResult |=GameConstant.MAHJONG_HU_CODE_ZI_MO;// 按自摸来查叫
                MahjongCheckResult result= xiantaoMahjongProcessor.getPlayerFanResult(pl,null,b,hu.fanResult,gt);
                if(result.fanNum>pl.getMaxFanNum())
                {
                    hudesc = pl.getHuDesc();
                    logger.info("   hudesc    " + hudesc);
                    pl.setMaxFanNum(result.fanNum);
                }
            }
        }
        pl.setHuDesc("查叫   "+hudesc);
        //
        logger.info("player id="+pl.getPlayerIndex()+" max fan="+pl.getMaxFanNum());
    }

    private void game_over(GameTable gt) {
        // 第一局结束扣房卡
        if (gt.isVipTable()) {
            if (!gt.isPayVipKa()) {
                if (!playerService.use_fangka(gt)) {
                    cleanTable(gt);
                    return;
                }
                gt.setPayVipKa(true);
            }
        }
        //牌局结束给每个人发个全量
        List<Player> plist=gt.getPlayers();
        for(int i=0;i<plist.size();i++)
        {
            Player p=plist.get(i);
            if (p != null && p.isNewVersion()){
                if(gt.isFull()){
                    enterTableDone(p,gt,true,true);
                }else{
                    enterTableDone(p,gt,false,true);
                }
            }
        }

        //发送游戏结束界面
        PlayerGameOverMsgAck gov=new PlayerGameOverMsgAck();
        gov.roomID=gt.getRoomID();
        gov.huCard=gt.getHu_card()&0xff;
        gov.dealerPos=gt.getDealerPos();
        gov.huPos = gt.getCurrentHuPlayerIndex();
        gov.baoCard = gt.getRiffraffCard();
        int b2=gt.getSecondBaoCard();
        int b3=gt.getLazarailloCard();
        int b4=gt.getSecondBaoCard2();
        gov.baoCard|=(b2<<8);
        gov.baoCard|=(b3<<16);
        gov.baoCard|=(b4<<24);
        //
        gov.init_players(gt.getPlayers(), gt);

        if (gt.isVipTable())
        {
            gov.isVipTable=1;
            gov.readyTime=systemConfigService.getPara(GameConfigConstant.CONF_VIP_GAME_OVER_ENTER_NEXT_TURN).getValueInt();
        }
        else
        {
            gov.isVipTable=0;
            gov.readyTime=30;
        }

        this.sendMsgToTable(gt,gov,1);
//		 this.resetTableSeq(gt);
        //

        //vip输赢累加
//		 List<Player>  plist=gt.getPlayers();
        if(gt.isVipTable())
        {
            for(int i=0;i<plist.size();i++)
            {
                Player pl=plist.get(i);
                //带正负号
                pl.setWinLoseTotal(pl.getWinLoseTotal() + pl.getWinLoseGoldNum());

                List<Byte> c = pl.getCardsInHand();

                c.size();
            }
        }
        //日志处理
        game_over_log(gt);


        gt.clearGameState();
        gt.setGangOpBackup(null);
        gt.setGangMsgBackup(null);
        gt.setAllPlayerState(GameConstant.PALYER_GAME_STATE_IN_TABLE_GAME_OVER_WAITING_TO_CONTINUE);
        //然后，游戏进入等待玩家继续界面
        gt.setState(GameConstant.GAME_TABLE_STATE_WAITING_PALYER_TO_CONTINUE);


        GameRoom gr = gameRoomMap.get(gt.getRoomID());


        //机器人处理
        if(!gt.isVipTable()){
            for(int i=0;i<plist.size();i++)
            {
                Player pl=plist.get(i);

                if(!pl.getOnTable()){
                    playerLeftTable(pl);
                }
                else if(pl.getGold()<gr.getMinGold()){
                    playerLeftTable(pl);
                }
                else if(pl.isRobot()){
                    pl.dcRobotgamenum();
                    if(pl.getRobotgamenum()<=0){
                        playerLeftTable(pl);
                    }
                }
            }
        }


        gt.setAllRobotReady();
        gt.setHandEndTime(System.currentTimeMillis());
        machineService.reset_machinetime_current(gt);
    }

    public void responseGpsInfo(Player stPlayer){
        GameRoom rm = gameRoomMap.get(stPlayer.getRoomID());
        if(rm == null)
            return;

        GameTable gt = rm.getTable(stPlayer.getTableID());
        if (gt == null)
            return;

        GetPlayersGpsInfoAck ack = new GetPlayersGpsInfoAck();

        List<Player> players = gt.getPlayers();
        for(int i = 0;i < players.size(); ++i)
        {
            Player pl = players.get(i);
            if (pl == null)
                continue;

            int tablePos = pl.getTablePos();
            logger.info("nick name is : " + pl.getPlayerName() + ", table pos is  " + tablePos
                    + ", latitude is : " + pl.getLatitude() + ", longitude is : " + pl.getLongitude());

            switch (tablePos) {
                case 0:
                    ack.player0Latitude = pl.getLatitude();
                    ack.player0Longitude = pl.getLongitude();
                    break;
                case 1:
                    ack.player1Latitude = pl.getLatitude();
                    ack.player1Longitude = pl.getLongitude();
                    break;
                case 2:
                    ack.player2Latitude = pl.getLatitude();
                    ack.player2Longitude = pl.getLongitude();
                    break;
                case 3:
                    ack.player3Latitude = pl.getLatitude();
                    ack.player3Longitude = pl.getLongitude();
                    break;
            }
        }

        ack.distance =  systemConfigService.getPara(GameConfigConstant.CONF_MINDISTANCE).getValueInt();

        ack.sessionID = stPlayer.getPlayerLobbySessionID();
        this.sendMsgToTable(gt,ack);
    }
}

