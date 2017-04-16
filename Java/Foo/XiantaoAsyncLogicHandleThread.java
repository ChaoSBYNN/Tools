package com.szxx.async.handler;

import com.sparkingfuture.common.util.DateUtil;
import com.szxx.base.server.GameSocketServer;
import com.szxx.constant.GameConfigConstant;
import com.szxx.domain.*;
import com.szxx.mahjong.xiantao.constant.GameConstant;
import com.szxx.mahjong.xiantao.mahjong.XiantaoMahjongProcessor;
import com.szxx.msg.PlayerOperationNotifyMsg;
import com.szxx.msg.PlayerTableOperationMsg;
import com.szxx.processor.mahjong.MahjongProcessor;
import com.szxx.service.IMachineService;
import com.szxx.service.IPlayerService;
import com.szxx.service.ISystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Description:
 * 仙桃异步逻辑线程
 *
 * @author chris
 */
@SuppressWarnings("ALL")
public class XiantaoAsyncLogicHandleThread extends AsyncLogicHandleThread{
    private static final Logger logger = LoggerFactory.getLogger(XiantaoAsyncLogicHandleThread.class);

    public XiantaoAsyncLogicHandleThread(ISystemConfigService cfgService, IMachineService machineService, MahjongProcessor mahjongProcessor, IPlayerService playerService) {
        super(cfgService, machineService, mahjongProcessor, playerService);
    }

    //CHAOS_ZHANG xiantao rule begin
    //CHAOS_ZHANG function guide:
    //CHAOS_ZHANG 1.player_chu_notify 玩家出牌提示
    //CHAOS_ZHANG 2.calLaiziFan 计算癞子番
    //CHAOS_ZHANG 3.player_op_hu 玩家操作-胡
    //CHAOS_ZHANG 4.auto_hu_other_player 自动胡操作-一炮多响
    //CHAOS_ZHANG 5.player_hu 玩家胡-信息
    //CHAOS_ZHANG 6.win_lose_gold_calculate_hu 输赢计算

    @Override
    public void execute_operation_msg(PlayerTableOperationMsg msg, Player pl) {
        if (msg == null || pl == null)
            return;


        //剩余多少张牌
        msg.cardLeftNum = gt.getCardLeftNum();
        //玩家出牌
        if ((msg.operation & GameConstant.MAHJONG_OPERTAION_CHU) == GameConstant.MAHJONG_OPERTAION_CHU) {
            if (pl.getOverTimeState() == GameConstant.PALYER_GAME_OVERTIME_STATE_IN_TABLE_WAITING_TO_OVERTIMECHU) {
                return;
            }
            playerChuOperation(gr, gt, msg, pl);

        } else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU) == GameConstant.MAHJONG_OPERTAION_OVERTIME_AUTO_CHU) {
            if (pl.getOverTimeState() == GameConstant.PALYER_GAME_OVERTIME_STATE_IN_TABLE_NOWAITING_TO_OVERTIMECHU)
                return;

            msg.operation = GameConstant.MAHJONG_OPERTAION_CHU;

            playerChuOperation(gr, gt, msg, pl);

        }
        //玩家吃牌
        else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_CHI) == GameConstant.MAHJONG_OPERTAION_CHI) {
            logger.error("无吃牌!");
        }

        ////玩家碰牌
        else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_PENG) == GameConstant.MAHJONG_OPERTAION_PENG) {
            player_op_peng(gr, gt, msg, pl);
        }

        //玩家杠牌，不管啥杠，客户端都发的这个明杠参数，服务器自己会判断是什么杠，客户端用这个MAHJONG_OPERTAION_MING_GANG
        else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_MING_GANG) == GameConstant.MAHJONG_OPERTAION_MING_GANG) {
            player_op_gang(gr, gt, msg, pl);

        }
        ////玩家去消吃碰牌
        else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_CANCEL) == GameConstant.MAHJONG_OPERTAION_CANCEL) {
            player_cancel_chi_peng_gang_hu(gr, gt, msg, pl);
        } else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_HU) == GameConstant.MAHJONG_OPERTAION_HU) {
            player_op_hu(gt, msg, pl);
        } else if ((msg.operation & GameConstant.MAHJONG_OPERTAION_POP_LAST) == GameConstant.MAHJONG_OPERTAION_POP_LAST) {
            //取得杠牌后抓的尾牌
            player_op_get_gang_card(gr, gt, msg, pl);
        }
    }

    @Override
    public void player_op_peng(GameRoom gr, GameTable gt, PlayerTableOperationMsg msg, Player pl) {
        int v1 = msg.getCardValue() & 0xff;
        //玩家出牌
        MahjongCheckResult waiting = gt.getWaitingPlayerOperate();
        if (waiting == null) {
            logger.error("peng,no such operation, player_index=" + pl.getPlayerIndex() + ",tableID=" + pl.getTableID());
            return;//当前没有在等待某个玩家操作；
        }

        if (waiting.playerTableIndex != pl.getTablePos()) {

            logger.error("peng,table position invalid, player_index=" + pl.getPlayerIndex() + ",tableID=" + pl.getTableID());
            return;//当前不是在等这个玩家操作
        }

        if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_PENG) != GameConstant.MAHJONG_OPERTAION_PENG) {
            logger.error("peng,operation invalid, player_index=" + pl.getPlayerIndex() + ",tableID=" + pl.getTableID());
            return;//当前不能碰；
        }

        if (waiting.getPengCardValue() != msg.getCardValue()) {
            logger.error("peng,parameter invalid, player_index=" + pl.getPlayerIndex() + ",tableID=" + pl.getTableID());
            return;//碰的牌不对；
        }

        if (v1 == 0) {
            v1 = waiting.getPengCardValue() & 0xff;
        }


        pl.setLastGangChuPlayer(null);
        pl.setCancelHu(false);
        pl.setCouldNotChuCards(0);
        //去掉可能的被抢杠前的记录
        gt.setGangOpBackup(null);
        gt.setGangMsgBackup(null);
        pl.retShifennum();
        pl.retShisanyaozi();



        //通知客户端把被碰的牌拿走
        if (gt.getChuPlayer() != null) {
            PlayerOperationNotifyMsg removeChuMsg = new PlayerOperationNotifyMsg();
            removeChuMsg.operation = GameConstant.MAHJONG_OPERTAION_REMOE_CHU_CARD;
            removeChuMsg.setPlayerTablePos(gt.getChuPlayer().getTablePos());
            removeChuMsg.target_card = v1;
            removeChuMsg.cardLeftNum = gt.getCardLeftNum();
            this.sendMsgToTable(gt, removeChuMsg, 1);
        }

        //
        //清理杠记录
        gt.setGangPlayer(null);

        int op = 0;

        //这时候要把当前操作的索引改成此玩家，这样他出牌的时候，当前操作玩家的索引才是正确的
        gt.setCurrentOpertaionPlayerIndex(pl.getTablePos());
        //

        pl.removeCardInHand(v1);
        pl.removeCardInHand(v1);
        gt.add_Down_cards((byte) v1);
        gt.add_Down_cards((byte) v1);
        //

        //发给其他玩家，让他们知道当前轮到谁操作
        PlayerOperationNotifyMsg msg2 = new PlayerOperationNotifyMsg();
        msg2.operation = GameConstant.MAHJONG_OPERTAION_TIP;
        msg2.cardLeftNum = gt.getCardLeftNum();
        msg2.setPlayerTablePos(pl.getTablePos());
        this.sendMsgToTable(gt, msg2, 1);

        //

        CardDown down = pl.addCardDown(v1, v1, v1, GameConstant.MAHJONG_OPERTAION_PENG);
        int card_down = down.cardValue;
        down.chuPlayer = gt.getChuPlayer();
        if (down.chuPlayer != null) {
            logger.info("peng ,chu pl=" + down.chuPlayer.getPlayerIndex());
            down.setChuOffset(down.chuPlayer.getTablePos(), pl.getTablePos());
            //
            msg.chuOffset = down.chuOffset;
        } else {
            logger.error("peng ,chu pl==null" + pl.getPlayerIndex());
        }

        // 楚雄 倒数第8 到 倒数 第5张 放杠胡牌，一个人出胡牌的金额
        down.leftCardNum = gt.getCardLeftNum();

        //设置碰的一组牌，客户端好表现
        msg.opValue = card_down;
        msg.setCardValue(v1 | (v1 << 8));
        this.sendMsgToTable(gt, msg, 1);


        //服务器清除等待玩家操作的数据
        gt.setWaitingPlayerOperate(null);

        //碰完了轮到他操作，进行打牌
        gt.setCurrentOpertaionPlayerIndex(pl.getTablePos());

        //等待客户端播动画
        Date dt = DateUtil.getCurrentUtilDate();
        gt.setWaitingStartTime(dt.getTime());
        gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_PLAYING_CHI_PENG_ANIMATION);

        gt.setCurrentCard((byte) 0);
        gt.setCardOpPlayerIndex(-1);
    }

    /**
     * 函数：玩家胡操作
     *
     * @param gt 牌局
     * @param msg 消息
     * @param pl 玩家
     */
    @Override
    public void player_op_hu(GameTable gt, PlayerTableOperationMsg msg, Player pl) {
        //
        MahjongCheckResult waiting = gt.getWaitingPlayerOperate();
        if (waiting == null) {
            logger.error("no such operation, PlayerIndex=" + pl.getPlayerIndex());
            return;//当前没有在等待某个玩家操作；
        }

        if (waiting.playerTableIndex != pl.getTablePos()) {

            logger.error("player_op_hu,table position invalid,player_index=" + pl.getPlayerIndex());
            return;//当前不是在等这个玩家操作
        }

        //
        if (((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_HU) != GameConstant.MAHJONG_OPERTAION_HU)) {
            logger.error("hu operation invalid");
            return;//当前不能吃；
        }

        MahjongCheckResult hu_xx = mahjongProcessor.checkWin(waiting.targetCard, pl, gt);
        if (hu_xx == null) {
            logger.error("playerName=" + pl.getPlayerName() + " hu operation failed,card=" + waiting.targetCard + " tableID=" + pl.getTableID());
            return;
        }

        if ((waiting.opertaion & GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA) == GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA) {
            hu_xx.fanResult |= GameConstant.MAHJONG_HU_GANG_SHANG_KAI_HUA;
        }



        // 杠上炮、抢杠胡
        hu_xx.addFanResult(waiting.fanResult);
        //记录胡的玩家
        List<Player> huPlayers = new ArrayList<Player>();
        //玩家胡了
        this.player_hu(gt, pl, waiting.chuPlayer, hu_xx.fanResult, hu_xx.targetCard, hu_xx.keZiNum, hu_xx.same2Shunzi, hu_xx);

        //剩下几家就自动胡了
        if(waiting.chuPlayer!=null )//看看是否有一炮多响
        {
            auto__hu_other_plaeyr(gt,hu_xx.targetCard, pl,waiting.chuPlayer.getTablePos());
        }
        //记录胡的玩家
        List<Player> players = gt.getPlayers();
        for (Player player : players) {
            if(player.isWin()) {
                huPlayers.add(player);
            }
        }
        //如果一炮多响，庄家就是放炮的人
        if (huPlayers.size() > 1 && pl.getHuPaoPL() != null) {
            logger.info("一炮多响,"+pl.getHuPaoPL().getPlayerName()+"玩家放铳");
            gt.setCurrentHuPlayerIndex(pl.getHuPaoPL().getTablePos());
        }
        //胡牌算分
        win_lose_gold_calculate_hu(gt,huPlayers,waiting.chuPlayer);

        //CHAOS_ZHANG ******************************************************************************
        //胡牌算分
        //mahjongProcessor.win_lose_gold_calculate_hu(gt,huPlayers,waiting.chuPlayer);

        game_over_hu(gt, false);
    }

    //CHAOS_ZHANG 玩家进行杠，看看有抢的没
    @Override
    protected void player_op_gang(GameRoom gr,GameTable gt,PlayerTableOperationMsg msg,Player pl) {
        byte v1=(byte)(msg.getCardValue()&0xff);

        logger.info("player_op_gang 1111, playerName={} card={},tableID={}",pl.getPlayerName(),Integer.toHexString(msg.cardValue),pl.getTableID());

        //玩家出牌
        MahjongCheckResult  waiting=gt.getWaitingPlayerOperate();
        if(waiting==null){
            logger.error("peng,no such operation, player_index={}, tableID={}",pl.getPlayerIndex(),pl.getTableID());
            return;//当前没有在等待某个玩家操作；
        }

        logger.info("player_op_gang 2222, playerName={}",pl.getPlayerName());
        if(waiting.playerTableIndex!=pl.getTablePos()){

            logger.error("peng,table position invalid, player_index= {}, tableID= {}",pl.getPlayerIndex(), pl.getTableID());
            return;//当前不是在等这个玩家操作
        }

        logger.info("player_op_gang 3333, playerName={},tableID={}",pl.getPlayerName(),pl.getTableID());
        if( waiting.find_gang(pl,msg.getCardValue())==false)
        {
            logger.error("gang,parameter invalid, player_index = {}, tableID= {}", pl.getPlayerIndex(), pl.getTableID());
            return;//gang的牌不对；
        }
        //
        //哪种形式的杠
        int gang_type=0;
        int newcard = 0;
        if((waiting.opertaion&GameConstant.MAHJONG_OPERTAION_MING_GANG)==GameConstant.MAHJONG_OPERTAION_MING_GANG )
        {
            logger.error("gang,ming gang, player_index = {}, tableID = {}", pl.getPlayerIndex(), pl.getTableID());
            gang_type=GameConstant.MAHJONG_OPERTAION_MING_GANG;
        }
        else
        {
            if((waiting.opertaion&GameConstant.MAHJONG_OPERTAION_AN_GANG)==GameConstant.MAHJONG_OPERTAION_AN_GANG ) {
                gang_type=GameConstant.MAHJONG_OPERTAION_AN_GANG;
            } else if((waiting.opertaion&GameConstant.MAHJONG_OPERTAION_BU_GANG)==GameConstant.MAHJONG_OPERTAION_BU_GANG ) {
                gang_type=GameConstant.MAHJONG_OPERTAION_BU_GANG;
            } else if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_PIZI_GANG) == GameConstant.MAHJONG_OPERTAION_PIZI_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_PIZI_GANG;
            } else if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_LAIZI_GANG) == GameConstant.MAHJONG_OPERTAION_LAIZI_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_LAIZI_GANG;
            }

            //补杠的时候是刚摸牌，把摸的放进来
            byte cd=pl.getCardGrab();
            if(cd!=0)
            {
                newcard = cd ;
                pl.addCardInHand(cd);
                pl.setCardGrab((byte)0);
            }
        }

        //
        if(gang_type==0)
        {
            logger.error("GANG,operation invalid, player_index={},tableID={}", pl.getPlayerIndex(),pl.getTableID());
            return;//当前不能gang；
        }

        v1= waiting.targetCard;

        //打印手牌
        print_player_log(pl,GameConstant.MAHJONG_OPERTAION_MING_GANG,v1);

        pl.setCancelHu(false);
        pl.clearLastCancelPengCard();
        pl.retShifennum();
        pl.retShisanyaozi();
        pl.setCouldNotChuCards(0);

        //2把牌放在桌子中间，如果没有吃碰胡之类，牌就放在这个玩家面前
        gt.setCurrentCard(v1);
        gt.setCardOpPlayerIndex(pl.getTablePos());

        PlayerTableOperationMsg mxg=new PlayerTableOperationMsg();
        mxg.operation=GameConstant.MAHJONG_OPERTAION_MING_GANG;//通知其他客户端，有人杠，都用这个明杠消息
        mxg.setCardValue(v1);
        msg.setPlayerTablePos(pl.getTablePos());
        mxg.useXiaoNum = waiting.useXiaojiNum;
        mxg.opValue=GameConstant.MAHJONG_OPERTAION_GANG_NOTIFY;//
        this.sendMsgToTable(gt,mxg,1);

        //CHAOS_ZHANG qianjiang end not has after ********************************************************
        //real_player_op_gang(gt, msg, pl);

        //CHAOS_ZHANG 抢杠胡
        if(gang_type==GameConstant.MAHJONG_OPERTAION_BU_GANG)//补杠可以抢胡
        {
            //按优先级，先胡
            waiting.addFanResult(GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU);
            MahjongCheckResult hu_result=table_loop_check(gt,v1,pl,GameConstant.MAHJONG_OPERTAION_HU,pl.getTablePos());
            if(hu_result != null)
            {
                //抢杠前先备份数据，万一人家不抢，可以恢复再杠
                waiting.gangOpPl=pl;
                gt.setGangOpBackup(waiting);

                gt.setGangMsgBackup(msg);
                //可能倍抢，先移走，等下如果对方不抢杠胡再放回来
                pl.removeCardInHand(v1);
                //

                //提示可以抢胡
                Player hu_pl=gt.getPlayerAtIndex(hu_result.playerTableIndex);
                //
                PlayerOperationNotifyMsg huMsg=new PlayerOperationNotifyMsg();
                huMsg.operation=GameConstant.MAHJONG_OPERTAION_HU;
                huMsg.setPlayerTablePos(hu_pl.getTablePos());
                huMsg.target_card=v1&0xff;
                huMsg.cardLeftNum=gt.getCardLeftNum();
                //
                hu_result.chuPlayer=pl;//记录被抢杠的人
                hu_result.fanResult |=GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU;//抢杠胡
                gt.setWaitingPlayerOperate(hu_result);


                logger.info("***********************抢杠胡***********************");
                //
                huMsg.sessionID=hu_pl.getPlayerLobbySessionID();
                //SystemConfig.gameSocketServer.sendMsg(hu_pl.getSession(), huMsg);
                this.sengMsgToPlayer(gt, hu_pl, huMsg);
                //
                long ctt=DateUtil.getCurrentUtilDate().getTime();
                hu_pl.setOpStartTime(ctt);
            }
            else
            {
                real_player_op_gang(gt,msg,pl);
            }
        }
        else
        {
            //暗杠不能抢
            //明杠，手里有3个，别人打一个，会先判断吃听，再杠，所以，也不需要判断抢杠，
            //明杠不用抢胡，胡之前已经先判断过了
            real_player_op_gang(gt,msg,pl);
        }
    }

    //CHAOS_ZHANG 玩家进行杠,检查完，没有要抢的， Player => XiantaoPlayer
    @Override
    protected void real_player_op_gang(GameTable gt, PlayerTableOperationMsg msg, Player pl) {
        //CHAOS_ZHANG 获取杠的牌
        int v1 = msg.getCardValue() & 0xff;
        logger.info("1111 real_gang, player={} card={},tableID={}" , pl.getPlayerName(), Integer.toHexString(msg.getCardValue()), pl.getTableID());

        // 玩家出牌
        MahjongCheckResult waiting = gt.getWaitingPlayerOperate();
        if (waiting == null) {
            logger.error("gang,no such operation, player_index={}, tableID={}" , pl.getPlayerIndex(), pl.getTableID());
            return;// 当前没有在等待某个玩家操作；
        }

        //
        if (waiting.playerTableIndex != pl.getTablePos()) {
            logger.error("gang,table position invalid, player_index={}, tableID={}", pl.getPlayerIndex(), pl.getTableID());
            return;// 当前不是在等这个玩家操作
        }

        //CHAOS_ZHANG 哪种形式的杠
        int gang_type = 0;
        if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_MING_GANG) == GameConstant.MAHJONG_OPERTAION_MING_GANG) {
            gang_type = GameConstant.MAHJONG_OPERTAION_MING_GANG;
        } else {
            if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_AN_GANG) == GameConstant.MAHJONG_OPERTAION_AN_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_AN_GANG;
            } else if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_BU_GANG) == GameConstant.MAHJONG_OPERTAION_BU_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_BU_GANG;
            } else if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_PIZI_GANG) == GameConstant.MAHJONG_OPERTAION_PIZI_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_PIZI_GANG;
            } else if ((waiting.opertaion & GameConstant.MAHJONG_OPERTAION_LAIZI_GANG) == GameConstant.MAHJONG_OPERTAION_LAIZI_GANG) {
                gang_type = GameConstant.MAHJONG_OPERTAION_LAIZI_GANG;
            }

        }

        if (gang_type == 0) {
            logger.error("GANG,operation invalid, player_index={}, tableID={}", pl.getPlayerIndex(), pl.getTableID());
            return;// 当前不能gang；
        }

        if (waiting.find_gang(pl, msg.getCardValue()) == false) {
            logger.error("gang,parameter invalid, player_index={},tableID={}", pl.getPlayerIndex(), pl.getTableID());
            return;// gang的牌不对；
        }

        msg.useXiaoNum = waiting.useXiaojiNum;

        // 真正杠了
        gt.setGangPlayer(pl);

        if (gang_type == GameConstant.MAHJONG_OPERTAION_MING_GANG && gt.getChuPlayer() != null) {
            // 通知客户端把被杠的牌拿走
            PlayerOperationNotifyMsg removeChuMsg = new PlayerOperationNotifyMsg();
            removeChuMsg.operation = GameConstant.MAHJONG_OPERTAION_REMOE_CHU_CARD;
            removeChuMsg.setPlayerTablePos(gt.getChuPlayer().getTablePos());
            removeChuMsg.target_card = msg.getCardValue();
            removeChuMsg.cardLeftNum = gt.getCardLeftNum();
            removeChuMsg.use_xiaoji = waiting.useXiaojiNum;
            this.sendMsgToTable(gt, removeChuMsg, 1);
        }

        // 这时候要把当前操作的索引改成此玩家，这样他出牌的时候，当前操作玩家的索引才是正确的
        gt.setCurrentOpertaionPlayerIndex(pl.getTablePos());
        //
        pl.setLastGangChuPlayer(null);
        //
        int card_down = 0;

        //CHAOS_ZHANG 计算当前回合飘癞子数 用于杠算分
        int laiziNum = gt.getVisibleCardNum(gt.getLazarailloCard());

        //CHAOS_ZHANG 明杠
        if (gang_type == GameConstant.MAHJONG_OPERTAION_MING_GANG) {
            gt.add_Down_cards((byte) v1);
            for (int j = 0; j < 4 - waiting.useXiaojiNum - 1; j++) {
                pl.removeCardInHand(v1);
                gt.add_Down_cards((byte) v1);
            }

            for (int j = 0; j < waiting.useXiaojiNum; j++) {
                pl.removeCardInHand(0x11);
                gt.add_Down_cards((byte) 0x11);
            }


            //CHAOS_ZHANG 仙桃杠 飘癞子 laiziNum
            //CHAOS_ZHANG XiantaoCardDown dd = pl.addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_MING_GANG,laiziNum);
            XiantaoCardDown dd = ((XiantaoPlayer)pl).addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_MING_GANG,laiziNum);
            /*CardDown dd = pl.addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_MING_GANG);*/
            dd.usexiaojinum = waiting.useXiaojiNum;

            dd.chuPlayer = waiting.chuPlayer;// 记录放杠的人，他掏钱
            card_down = dd.cardValue;
            //
            if (dd.chuPlayer != null) {
                logger.info("ming gang ,chu pl={},tableID={}" ,
                        dd.chuPlayer.getPlayerIndex(), pl.getTableID());
                dd.setChuOffset(dd.chuPlayer.getTablePos(), pl.getTablePos());
                //
                msg.chuOffset = dd.chuOffset;
                //
                pl.setLastGangChuPlayer(waiting.chuPlayer);
            } else {
                logger.error("ming gang ,gang pl==null pl.getTableID(),tableID={}", pl.getPlayerIndex(), pl.getTableID());
            }

        } else if (gang_type == GameConstant.MAHJONG_OPERTAION_AN_GANG) {
            // 把摸的牌先放进来
            byte moCard = pl.getCardGrab();
            if (moCard != 0) {
                pl.addCardInHand(moCard);
                pl.setCardGrab((byte) 0);
            }
            // 暗杠的时候,如果有小鸡的话,要把小鸡移走并替换一张牌
            for (int i = 0; i < waiting.useXiaojiNum; i++) {
                pl.removeCardInHand(0x11);
                gt.add_Down_cards((byte) 0x11);
            }
            int removeCardsNum = 4 - msg.useXiaoNum;
            for (int i = 0; i < removeCardsNum; i++) {
                pl.removeCardInHand(v1);
                gt.add_Down_cards((byte) v1);
            }

            //CHAOS_ZHANG XiantaoCardDown dd = pl.addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_AN_GANG,laiziNum);
            /*CardDown dd = pl.addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_AN_GANG);*/
            XiantaoCardDown dd = ((XiantaoPlayer)pl).addGangCardDown(msg.getCardValue(), GameConstant.MAHJONG_OPERTAION_AN_GANG,laiziNum);
            card_down = dd.cardValue;
            dd.usexiaojinum = waiting.useXiaojiNum;

            put_not_hu_player(gt, dd, pl);

        } else if (gang_type == GameConstant.MAHJONG_OPERTAION_BU_GANG) {
            if (waiting.useXiaojiNum > 0) {
                pl.removeCardInHand(0x11);
                gt.add_Down_cards((byte) 0x11);
            } else {
                pl.removeCardInHand(v1);
                gt.add_Down_cards((byte) v1);
            }

            //CHAOS_ZHANG  XiantaoCardDown dd = pl.bu_gang(msg.getCardValue(),laiziNum);
            /*CardDown dd = pl.bu_gang(msg.getCardValue());*/
            XiantaoCardDown dd = ((XiantaoPlayer)pl).bu_gang(msg.getCardValue(),laiziNum);
            card_down = dd.cardValue;
            msg.chuOffset = dd.chuOffset;
            dd.usexiaojinum = waiting.useXiaojiNum;

            //
            put_not_hu_player(gt, dd, pl);
        } else if (gang_type == GameConstant.MAHJONG_OPERTAION_LAIZI_GANG) {
            pl.removeCardInHand(v1);
            gt.add_Down_cards((byte) v1);
            //
            XiantaoCardDown dd = ((XiantaoPlayer)pl).addGangCardDown(msg.getCardValue(), gang_type,laiziNum);
            card_down = dd.cardValue;

            //
            put_not_hu_player(gt, dd, pl);
        }

        // 发给其他玩家，让他们知道当前轮到谁操作
        PlayerOperationNotifyMsg msg2 = new PlayerOperationNotifyMsg();
        msg2.operation = GameConstant.MAHJONG_OPERTAION_TIP;
        msg2.cardLeftNum = gt.getCardLeftNum();
        msg2.setPlayerTablePos(pl.getTablePos());
        msg2.use_xiaoji = msg.useXiaoNum;
        this.sendMsgToTable(gt, msg2, 1);

        //
        // 杠的话，玩家手里有的这个牌都给他放倒
        // card_down=v1|(v1<<8)|(v1<<16)|(v1<<24);
        card_down = msg.getCardValue();
        // 设置碰的一组牌，客户端好表现
        msg.operation = gang_type;
        msg.opValue = card_down;
        msg.setCardValue(card_down);
        // 暗杠不要把牌发给其他玩家
        this.sendMsgToTable(gt, msg, 1);

        // 服务器清除等待玩家操作的数据
        gt.setWaitingPlayerOperate(null);

        // 碰完了轮到他操作，进行打牌
        gt.setCurrentOpertaionPlayerIndex(pl.getTablePos());

        // 等待客户端播动画
        Date dt = DateUtil.getCurrentUtilDate();
        gt.setWaitingStartTime(dt.getTime());
        gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_PLAYING_GANG_ANIMATION);
        //
        gt.setCurrentCard((byte) 0);
        gt.setCardOpPlayerIndex(-1);

        // 杠玩告诉玩家选牌
        // 杠完摸一张
        if (pl.getCardNumInHand() % 3 == 1) {
            // 给玩家摸一张
            byte b = gt.popLastCard(1); // 要摸最后一张牌
            // b=(byte)0x21;
            pl.setCardGrab(b);// 先摸一张，等动画播完
        }
    }

    /**
     * 函数：玩家胡信息
     *
     * @param gt 牌局
     * @param pl 玩家
     * @param dianPaoPl 点炮玩家
     * @param fanResult 番结果
     * @param huCard 所胡的牌
     * @param keZiNum 刻子数
     * @param shuangPuZi
     * @param hu 胡的结果
     */
    @Override
    public void player_hu(GameTable gt, Player pl, Player dianPaoPl, int fanResult, byte huCard, int keZiNum, int shuangPuZi, MahjongCheckResult hu) {
        //打印手牌
        print_player_log(pl, GameConstant.MAHJONG_OPERTAION_HU, huCard);

        //
        //玩家已经胡牌
        pl.playerHu(huCard, dianPaoPl, fanResult);
        //统计
        if (gt.isVipTable() && dianPaoPl != null) {
            dianPaoPl.setDianpaoCount(dianPaoPl.getDianpaoCount() + 1);//点炮+1
        }
        //只记录第一个胡的人
        if (gt.getCurrentHuPlayerIndex() == -1) {
            gt.setCurrentHuPlayerIndex(pl.getTablePos());
        }

        //通知所有玩家，有人胡了
        PlayerOperationNotifyMsg huMsg = new PlayerOperationNotifyMsg();
        huMsg.operation = GameConstant.MAHJONG_OPERTAION_PLAYER_HU_CONFIRMED;
        //CHAOS_ZHANG huMsg.player_table_pos = pl.getTablePos(); => huMsg.setPlayerTablePos(pl.getTablePos());
        huMsg.setPlayerTablePos(pl.getTablePos());
        huMsg.target_card = huCard;
        huMsg.setChiCardValue(pl.getFanType());
        huMsg.cardLeftNum = gt.getCardLeftNum();

        this.sendMsgToTable(gt, huMsg, 1);

    }

    /**
     * 函数：剩下几家自动胡 查看是否存在一炮多响
     *
     * @param gt 牌局
     * @param card 桌上打出的牌
     * @param opPlayer 当前操作的玩家
     * @param chuCardPlayerTableIndex 出牌玩家桌号
     */
    @Override
    protected MahjongCheckResult auto__hu_other_plaeyr(GameTable gt,byte card,Player opPlayer,int chuCardPlayerTableIndex) {

        List<Player> plist=gt.getPlayers();
        int pl_index=opPlayer.getTablePos()+1;
        if(pl_index>=plist.size())
            pl_index=0;
        //
        Player chuPl=gt.getPlayerAtIndex(chuCardPlayerTableIndex) ;
        //

        for(int i=0;i<plist.size()-1;i++)
        {
            Player pl=plist.get(pl_index);

            if(pl.getTablePos()==chuCardPlayerTableIndex)
                break;

            if(pl.isWin()==false)
            {
                //先检测卡胡
                MahjongCheckResult hu=mahjongProcessor.checkWin(card, pl,gt);

                if (hu != null) {
                    int waitFan = getWaitFan(gt);
                    hu.addFanResult(waitFan);
                }
                //
                if(hu!=null)
                {
                    this.player_hu(gt, pl,chuPl,hu.fanResult,hu.targetCard,hu.keZiNum,hu.same2Shunzi,hu);
                }
            }
            pl_index++;
            if(pl_index>=plist.size())
                pl_index=0;
        }

        //
        return null;
    }

    /**
     * 函数：玩家出牌提示
     *
     * @param gt 牌局
     * @param couldHu 能胡
     * @param couldGang 能杠
     */
    @Override
    protected void player_chu_notify(GameTable gt,boolean couldHu,boolean couldGang) {
        Player plx=gt.getCurrentOperationPlayer();
        if(plx==null)
            return;

        logger.info("player_chu_notify, playerName= {} tableID = {}", plx.getPlayerName(), plx.getTableID());
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
                player_could_hu=true;
            }

        }

        //提示玩家出牌
        msg.operation |=GameConstant.MAHJONG_OPERTAION_CHU;
        msg.setPlayerTablePos(plx.getTablePos());
        msg.setChiCardValue(newCard);
        msg.cardLeftNum=gt.getCardLeftNum();
        msg.setChiFlag(0);//plx.getCouldNotChuCards();
        //

        //自己摸，看看各种杠
        MahjongCheckResult gang_result=null;
        if(couldGang)
        {
            gang_result=mahjongProcessor.checkGang(gt,newCard,plx,true);
        }

        //
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
        GameSocketServer.sendMsg(plx.getSession(), msg);
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
     * 函数：输赢分数计算
     *
     * @param gt 牌局
     * @param pl 玩家
     * @param moCard
     * @param pao_pl 点炮玩家
     * @param fanResult 番结果
     */
    protected void win_lose_gold_calculate_hu(GameTable gt, List<Player> huPlayers, Player pao_pl) {

        int manFan = cfgService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getValueInt();
        int difen = cfgService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getPro_1();

        logger.info("开始计算胡牌分数");
        if (pao_pl != null && huPlayers != null) {
            for (Player huPlayer : huPlayers) {
                if (pao_pl.getPlayerID().equals(huPlayer.getPlayerID())) {
                    logger.error("win_lose_gold_calculate_hu, error");
                    return;
                }
            }
        }

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

        //胡牌
        if(huPlayers!=null) {
            for (Player huPlayer : huPlayers) {
                //CHAOS_ZHANG 如果4张癞子均由hu_player 打出 player.setBao2Score(laiziFan*2); 32倍
                if(huPlayer.getCardXNumAll(gt.getLazarailloCard())==4){
                    baseLaiziFan = 32;
                }
                boolean yinghu = true;
                if((huPlayer.getHuFanResult()&GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI)!=0){
                    yinghu = false;//癞子归位、无癞子、做将不算归位
                }
                String hu_str = "胡";
                int hu_fan = 0;
                //TODO add qiang gang hu
                if (pao_pl == null) {
                    //CHAOS_ZHANG 自摸没有点炮
                    huPlayer.setFanType(huPlayer.getFanType() | GameConstant.MAHJONG_HU_CODE_WIN | GameConstant.MAHJONG_HU_CODE_ZI_MO);
                    //CHAOS_ZHANG 黑摸：（硬胡自摸）底分的4倍，3家付钱;3x4x底分=12
                    //CHAOS_ZHANG 软摸：（有一个赖子的自摸）底分的2倍，3家付钱;3x2x底分=6

                    if (yinghu) {//硬胡自摸
                        hu_fan = 4 ;
                        hu_str = "黑摸胡牌";
                    } else {
                        hu_fan = 2 ;
                        hu_str = "自摸胡牌";
                    }
                    logger.info(huPlayer.getPlayerIndex()+":"+hu_str+"翻数:"+hu_fan);
                    //计算自摸
                    int total_hu_win = 0;

                    for (Player player : plist) {
                        //CHAOS_ZHANG 自摸胡三家
                        int hu_lose = 0;
                        if (player.getPlayerID().equals(huPlayer.getPlayerID())) {
                            continue;
                        }
                        //CHAOS_ZHANG 输家分数
                        hu_lose = huPlayer.getBao2Score() * player.getBao2Score() * hu_fan;
                        //CHAOS_ZHANG 赢家分数总计
                        total_hu_win += hu_lose;
                        //CHAOS_ZHANG 计算输家最后得分 杠得扣分 + 其他规则 + 输掉得分
                        //CHAOS_ZHANG * baseLaiziFan 乘以癞子番数
                        player.setWinLoseGoldNum(player.getWinLoseGoldNum() + (0 - (hu_lose * baseLaiziFan) ));
                        //CHAOS_ZHANG 输牌描述 e.g. 黑摸硬胡-4
                        player.setHuDesc(player.getHuDesc()+hu_str + "-" + (hu_lose * baseLaiziFan));
                        logger.info(player.getPlayerIndex()+":输掉的分数=赢家癞子翻数x输家癞子翻数X胡牌翻数:"+player.getWinLoseGoldNum()+"-"+huPlayer.getBao2Score()+"X"+player.getBao2Score()+"X"+hu_fan+"="+hu_lose* baseLaiziFan);
                    }

                    //CHAOS_ZHANG 赢家得分总计 杠锝扣分 + 其他规则 + 赢到的总分
                    //CHAOS_ZHANG * baseLaiziFan 乘以癞子番数
                    huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + (total_hu_win * baseLaiziFan));
                    //CHAOS_ZHANG 赢牌描述 e.g. 黑摸硬胡+12
                    huPlayer.setHuDesc(huPlayer.getHuDesc()+hu_str+"+" + (total_hu_win * baseLaiziFan));
                    logger.info(huPlayer.getPlayerIndex()+":赢得分数=其他玩家输的总分数"+total_hu_win* baseLaiziFan);
                } else {
                    //CHAOS_ZHANG 抢杠胡
                    if((huPlayer.getHuFanResult()&GameConstant.MAHJONG_HU_CODE_QIANG_GANG_HU)!=0){

                        hu_fan=1;
                        hu_str = "抢杠胡";
                        int count_win = 0;

                        for (Player player : plist) {

                            if(huPlayer.equals(player)){
                                continue;
                            }
                            logger.info(player.getPlayerIndex()+":玩家抢杠胡");
                            logger.info(huPlayer.getPlayerIndex()+":"+hu_str+"翻数:"+hu_fan);
                            huPlayer.setFanType(huPlayer.getFanType() | GameConstant.MAHJONG_HU_CODE_WIN | GameConstant.MAHJONG_HU_CODE_DIAN_PAO);
                            int dianpao_lose = huPlayer.getBao2Score() * player.getBao2Score() * hu_fan;
                            count_win+=dianpao_lose;
                            logger.info(huPlayer.getPlayerIndex()+":赢得分数=玩家输的分数"+dianpao_lose);
                            //CHAOS_ZHANG * baseLaiziFan 乘以飘癞子 番数
                            player.setWinLoseGoldNum(player.getWinLoseGoldNum() + (0 - (dianpao_lose  * baseLaiziFan)));
                            player.setHuDesc(player.getHuDesc()+"抢杠胡-" + (dianpao_lose  * baseLaiziFan));
                            logger.info(player.getPlayerIndex()+":输掉的分数=赢家癞子翻数x输家癞子翻数X胡牌翻数:"+huPlayer.getBao2Score()+"X"+player.getBao2Score()+"X"+hu_fan+"="+dianpao_lose);


                        }
                        huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + (count_win  * baseLaiziFan));
                        huPlayer.setHuDesc(huPlayer.getHuDesc()+hu_str+"+" + (count_win  * baseLaiziFan));

                    } else {
                        //计算点炮胡牌
                        //CHAOS_ZHANG 屁胡：底分的2倍，放铳给你的那1家付钱；（屁胡也叫黑胡）胡牌玩家得分等于1x2=2
                        hu_fan=2;
                        hu_str = "放铳胡牌";

                        logger.info(pao_pl.getPlayerIndex()+":玩家放铳");
                        logger.info(huPlayer.getPlayerIndex()+":"+hu_str+"翻数:"+hu_fan);

                        huPlayer.setFanType(huPlayer.getFanType() | GameConstant.MAHJONG_HU_CODE_WIN | GameConstant.MAHJONG_HU_CODE_DIAN_PAO);

                        //CHAOS_ZHANG 输家放铳 输掉的分数
                        int dianpao_lose = huPlayer.getBao2Score() * pao_pl.getBao2Score() * hu_fan;

                        //CHAOS_ZHANG 赢家操作
                        huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + (dianpao_lose  * baseLaiziFan));
                        huPlayer.setHuDesc(huPlayer.getHuDesc()+hu_str+"+" +  (dianpao_lose  * baseLaiziFan));
                        logger.info(huPlayer.getPlayerIndex()+":赢得分数=玩家输的分数"+dianpao_lose);

                        //CHAOS_ZHANG 输家操作
                        pao_pl.setWinLoseGoldNum(pao_pl.getWinLoseGoldNum() + (0 - (dianpao_lose  * baseLaiziFan)));
                        pao_pl.setHuDesc(pao_pl.getHuDesc()+"放铳-" +  (dianpao_lose  * baseLaiziFan));
                        logger.info(pao_pl.getPlayerIndex()+":输掉的分数=赢家癞子翻数x输家癞子翻数X胡牌翻数:"+huPlayer.getBao2Score()+"X"+pao_pl.getBao2Score()+"X"+hu_fan+"="+dianpao_lose);

                    }

                }
            }
        }

        //CHAOS_ZHANG 手上没有癞子，没有打过癞子，才能胡热铳
        //CHAOS_ZHANG 杠上炮 放热铳 标识
        boolean isGangShangPao = false;
        if(huPlayers!=null && huPlayers.size()>0 && pao_pl!=null && gt.getGangPlayer() != null && gt.getGangPlayer().getPlayerID().equals(pao_pl.getPlayerID())) {
            isGangShangPao = true;
        }
        //计算补杠、明杠牌的分数
        byte pizi = gt.getRiffraffCard();
        logger.info("开始计算杠牌分数");
        for (Player player : plist) {
            int i=0;
            //CHAOS_ZHANG Player to XiantaoPlayer
            for (XiantaoCardDown cd : ((XiantaoPlayer)player).getCardsDownXiantao()) {

                i++;
                int size = player.getCardsDown().size();//玩家出牌列表，不含癞子杠
                //杠上炮玩家除癞子外最后一次出牌(杠,痞子碰)，不算赢分
                if(isGangShangPao && player.getPlayerID().equals(pao_pl.getPlayerID()) && i==size){
                    continue;
                }

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

        //CHAOS_ZHANG 杠上炮是否计算杠玩家
        //胡了，计算杠上炮
        if(huPlayers!=null) {
            //CHAOS_ZHANG 手上没有癞子，没有打过癞子，才能胡热铳
            int laiziCard = gt.getLazarailloCard();

            //CHAOS_ZHANG 飘过癞子 不用计算杠上炮 只能自摸
            if (gt.getVisibleCardNum((byte)laiziCard) == 0) {

                logger.info("开始计算杠上炮");
                for (Player huPlayer : huPlayers) {

                    //CHAOS_ZHANG huPlayer.getCardXNumAll(laiziCard)==0 手上没有癞子，没有打过癞子，才能胡热铳
                    if (huPlayer != null && huPlayer.getCardXNumAll(laiziCard) == 0 && pao_pl != null && gt.getGangPlayer() != null && gt.getGangPlayer().getPlayerID().equals(pao_pl.getPlayerID())) {
                        List<CardDown> cardsDown = gt.getGangPlayer().getCardsDown();
                        if (cardsDown.size() < 1) {
                            break;//开局癞子杠，出牌有人胡的情况
                        }
                        CardDown cd = cardsDown.get(cardsDown.size() - 1);//看除了癞子杠外最后一张牌是不是杠

                        //杠上炮，杠的人不能收杠的钱,杠应该收得的钱给胡牌的人
                        //CHAOS_ZHANG 回头笑 自笑热铳
                        if (cd.type == GameConstant.MAHJONG_OPERTAION_BU_GANG) {
                            //CHAOS_ZHANG 杠得分总计
                            int total_gangshangpao_win = 0;
                            for (Player gang_player : plist) {
                                //CHAOS_ZHANG 杠的分数
                                int gang_lose = 0;
                                if (gt.getGangPlayer().getPlayerID().equals(gang_player.getPlayerID())) {
                                    continue;
                                }
                                //CHAOS_ZHANG 自笑热铳  自笑玩家得到 3*1分 分数转移
                                int gangScore = 3*1;
                                //CHAOS_ZHANG 放铳玩家 扣分其他玩家 不需要扣分
                                gang_lose = gt.getGangPlayer().getBao2Score() * gang_player.getBao2Score() * gangScore;
                                total_gangshangpao_win += gang_lose;

                            }
                            //CHAOS_ZHANG 把点炮的玩家杠的分减去 gang_player score to hu_player score
                            gt.getGangPlayer().setWinLoseGoldNum(gt.getGangPlayer().getWinLoseGoldNum() - total_gangshangpao_win);
                            gt.getGangPlayer().setHuDesc(gt.getGangPlayer().getHuDesc() + " 自笑热铳-" + total_gangshangpao_win);

                            huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + total_gangshangpao_win);
                            huPlayer.setHuDesc(huPlayer.getHuDesc() + " 自笑热铳+" + total_gangshangpao_win);

                            logger.info(gt.getGangPlayer().getPlayerIndex() + ":玩家补杠牌:" + cd.cardValue + "放铳扣分:" + total_gangshangpao_win);
                            logger.info(huPlayer.getPlayerIndex() + ":玩家杠上炮:" + cd.cardValue + "赢分:" + total_gangshangpao_win);

                        }

                        //CHAOS_ZHANG 闷笑热铳
                        if (cd.type == GameConstant.MAHJONG_OPERTAION_AN_GANG) {

                            int total_gangshangpao_win = 0;
                            for (Player gang_player : plist) {
                                int gang_lose = 0;
                                if (gt.getGangPlayer().getPlayerID().equals(gang_player.getPlayerID())) {
                                    continue;
                                }
                                //CHAOS_ZHANG 黑笑热铳  自笑玩家得到 3*2分 分数转移
                                int gangScore = 3*2;
                                //有人点炮，其他玩家不用付杠的分，要把前面的杠的分加上去
                                gang_lose = gt.getGangPlayer().getBao2Score() * gang_player.getBao2Score() * gangScore;
                                total_gangshangpao_win += gang_lose;


                            }
                            //CHAOS_ZHANG 把点炮的玩家杠的分减去 gang_player score to hu_player score
                            gt.getGangPlayer().setWinLoseGoldNum(gt.getGangPlayer().getWinLoseGoldNum() - total_gangshangpao_win);
                            gt.getGangPlayer().setHuDesc(gt.getGangPlayer().getHuDesc() + " 黑笑热铳-" + total_gangshangpao_win);

                            huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + total_gangshangpao_win);
                            huPlayer.setHuDesc(huPlayer.getHuDesc() + " 黑笑热铳+" + total_gangshangpao_win);

                            logger.info(gt.getGangPlayer().getPlayerIndex() + ":玩家补杠牌:" + cd.cardValue + "放铳扣分:" + total_gangshangpao_win);
                            logger.info(huPlayer.getPlayerIndex() + ":玩家杠上炮:" + cd.cardValue + "赢分:" + total_gangshangpao_win);

                        }

                        //CHAOS_ZHANG 点笑热铳
                        if (cd.type == GameConstant.MAHJONG_OPERTAION_MING_GANG || (cd.type == GameConstant.MAHJONG_OPERTAION_PENG && pizi == (byte) cd.cardValue)) {

                            //CHAOS_ZHANG 点笑热铳  自笑玩家得到 1分 分数转移
                            int gangScore = 1;
                            int gangshangpao_lose = gt.getGangPlayer().getBao2Score() * cd.chuPlayer.getBao2Score() * gangScore;

                            //CHAOS_ZHANG 把点炮的玩家杠的分减去 gang_player score to hu_player score
                            gt.getGangPlayer().setWinLoseGoldNum(gt.getGangPlayer().getWinLoseGoldNum() - gangshangpao_lose);
                            gt.getGangPlayer().setHuDesc(gt.getGangPlayer().getHuDesc() + " 点笑热铳-" + gangshangpao_lose);

                            huPlayer.setWinLoseGoldNum(huPlayer.getWinLoseGoldNum() + gangshangpao_lose);
                            huPlayer.setHuDesc(huPlayer.getHuDesc() + " 点笑热铳+" + gangshangpao_lose);

                            logger.info(gt.getGangPlayer().getPlayerIndex() + ":玩家补杠牌:" + cd.cardValue + "放炮扣分:" + gangshangpao_lose);
                            logger.info(huPlayer.getPlayerIndex() + ":玩家杠上炮:" + cd.cardValue + "赢分:" + gangshangpao_lose);
                        }
                        //其他情况不算杠上炮
                    }
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

    @Override
    protected void playerChuOperation(GameRoom gr, GameTable gt, PlayerTableOperationMsg msg, Player pl) {

        logger.info("当前玩家="+pl.getPlayerName()+"操作出牌="+Integer.toHexString(msg.cardValue)+",tableID="+pl.getTableID());

        //CHAOS_ZHANG 修复对家出牌不能胡
        for(Player playerCancelToClear : gt.getPlayers()){
            playerCancelToClear.setCancelHu(false);
        }

        int idx = gt.getCurrentOpertaionPlayerIndex();
        if (idx != pl.getTablePos()) {
            logger.error("table_pos 当前没轮到此玩家操作=" + pl.getPlayerIndex() + ",tablePos=" + pl.getTablePos()
                    + " currentIdx=" + idx + ",tableID=" + pl.getTableID());
            return;
        }

        if (gt.getPlaySubstate() != GameConstant.GAME_TABLE_SUB_STATE_IDLE) {
            logger.error("sub state 当前没轮到此玩家操作=" + pl.getPlayerIndex() + ",tablePos=" + pl.getTablePos());
            return;
        }
        // 玩家出牌
        byte card_value = (byte) (msg.cardValue & 0xff);

        // 记录出牌的人
        gt.setChuPlayer(pl);

        // 清除胡牌
        pl.setCancelHu(false);

        // 清理最近取消碰的牌
        pl.clearLastCancelPengCard();

        print_player_log(pl, GameConstant.MAHJONG_OPERTAION_CHU, card_value);

        // 出牌次数加1
        pl.setChuPaiNum(pl.getChuPaiNum() + 1);

        int pl_pos = msg.playerTablePos;
        // 看看之前刚摸的牌有没有，摸的牌，先放cardgrab变量里面，等他出牌的时候再放进去，这样断线重连的时候，他摸的还是原来那张
        byte moCard = pl.getCardGrab();
        if (moCard != 0) {
            pl.addCardInHand(moCard);
            pl.setCardGrab((byte) 0);
        }

        // 如果玩家手中的牌不够，先让他摸一张
        if (pl.getCardNumInHand() % 3 != 2) {
            logger.error("playerName=" + pl.getPlayerName() + " 手中牌不够，" + Integer.toHexString(card_value) + " pl_pos="
                    + pl_pos + " pl.getTablePos()=" + pl.getTablePos() + ",tableID=" + pl.getTableID());
            player_mo(gt);
            return;
        }

        byte card_v = pl.findCardInHand(card_value);
        //
        if (card_v == card_value && pl_pos == pl.getTablePos()) {
            // 1把牌从玩家手里拿走
            pl.removeCardInHand(card_value);

            gt.add_Down_cards(card_value);
            //
            // 2把牌放在桌子中间，如果没有吃碰胡之类，牌就放在这个玩家面前
            gt.setCurrentCard(card_v);
            gt.setCardOpPlayerIndex(pl.getTablePos());
            //
            this.sendMsgToTableExceptMe(gt, msg, pl.getPlayerID(), 1);

            // 有问题，不能用
            // 给出牌玩家也发一条出牌信息，包含手牌、出牌、吃碰牌信息，用于服务端、客户端数据同步
            msg.handCards = pl.getCardsInHand();
            msg.beforeCards = pl.getCardsBefore();
            msg.downCards = pl.getCardsDown();
            msg.setSessionID(pl.getPlayerLobbySessionID());
            GameSocketServer.sendMsgWithSeq(pl.getSession(), msg, pl.getPlayerID(), gt);
            // 服务器清除等待玩家操作的数据
            // gt.setWaitingPlayerOperate(null);
            // 等待客户端播动画
            Date dt = DateUtil.getCurrentUtilDate();
            gt.setWaitingStartTime(dt.getTime());
            gt.setPlaySubstate(GameConstant.GAME_TABLE_SUB_STATE_PLAYING_CHU_ANIMATION);
            pl.chu_deal(card_value);
            // 提示胡牌
            // update_player_hu_card_list(gt,pl);
        } else {
            logger.error("出牌错误,playerName={},card_v={},card_value:{},pl_pos={},pl.getTablePos()={},tableID={}",
                    pl.getPlayerName(), Integer.toHexString(card_v), Integer.toHexString(card_value), pl_pos,
                    pl.getTablePos(), pl.getTableID());
            // player_mo(gt);
        }
    }

    //CHAOS_ZHANG xiantao rule end

    //查叫
    private void chajiao(GameTable gt) {
        int manFan = cfgService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getValueInt();
        int difen = cfgService.getPara(GameConfigConstant.CONF_QU_JING_MAN_FAN).getPro_1();

        if(gt.isGoldChangeCommited())
        {
            logger.info("ERROR========game_over_hu call twice=================================");
            return;
        }
        //防止算两次
        gt.setGoldChangeCommited(true);
        //
        game_over_update_vip(gt, true);
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
                    hasPlTing=true;
                    //有人胡牌
                    liu_ju_no_player_hu=false;
                    pl.setFanType(pl.getFanType()| GameConstant.MAHJONG_HU_CODE_TING);
                }
                else
                {
                    hasPlNotTing=true;
                }
            }
        }
        //
        gt.setLiuJuNoPlayerHu(liu_ju_no_player_hu);
        //
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
            if(plx.isTingCard())
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
//							modify_player_gold(gt, pl, -score, remark,"被查叫");

                        logger.info(remark);
                    }
                }
                //
                if(total_gold>0)
                {
                    String remark = String.format("查叫,idx=%d 赢,gold=%d",plx.getPlayerIndex(),total_gold);
                    //
                    logger.info("{} 胡翻前: {},  翻数: {}"+remark, plx.getHuFanNum(),fan);
                    plx.setWinLoseGoldNum(plx.getWinLoseGoldNum()+total_gold);
                    plx.setFanType(GameConstant.MAHJONG_HU_CODE_WIN|GameConstant.MAHJONG_HU_CODE_CHA_LIU_JU);
                    //
                    int fanType=GameConstant.MAHJONG_HU_CODE_WIN|GameConstant.MAHJONG_HU_CODE_CHA_LIU_JU;
                    plx.setHuFanNum(plx.getHuFanNum()+total_gold);
                    logger.info("{}  胡翻 后  {}", remark, plx.getHuFanNum());

                }
            }
        }
        logger.info("game over hu,tableID={},vipTableID={},hand:{},handTotal:{}",
                gt.getTableID(),gt.getVipTableID(),gt.getHandNum(), gt.getHandsTotal());

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

        //
        long ctt= DateUtil.getCurrentUtilDate().getTime();
        gt.setHandEndTime(ctt);
    }

    /**
     * 清理牌桌
     *
     * @param gt
     */
    private void cleanTable(GameTable gt){
        if(gt!=null)
        {
            logger.error("房间ID:{} 桌子ID：{},{} 桌子状态State：{} 当前人数：{}",
                    gt.getRoomID(), gt.getTableID(), gt.getVipTableID(), gt.getState(),gt.getPlayerNum());
            logger.error("本局开始时间：{} 当前操作人currentOpertaionPlayerIndex:{}",
                    new Date(gt.getHandStartTime()),gt.getCurrentOpertaionPlayerIndex());
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

                    //清理要把所有vip设置回0
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

    /**
     * 玩家执行杠牌
     * @param gr
     * @param gt
     * @param msg
     * @param pl
     */
    @Override
    protected void player_op_get_gang_card(GameRoom gr, GameTable gt, PlayerTableOperationMsg msg, Player pl){
        MahjongCheckResult waiting=gt.getWaitingPlayerOperate();

        if(waiting==null){
            logger.error("no such operation, PlayerIndex=" + pl.getPlayerIndex());
            return;//当前没有在等待某个玩家操作；
        }

        if(waiting.playerTableIndex!=pl.getTablePos()){

            logger.error("player_op_get_gang_card,table position invalid,player_index="+pl.getPlayerIndex());
            return;//当前不是在等这个玩家操作
        }

//		吃，或者吃听
        if((waiting.opertaion&GameConstant.MAHJONG_OPERTAION_POP_LAST)!=GameConstant.MAHJONG_OPERTAION_POP_LAST)
        {
            logger.error("operation invalid");
            return;//当前不能吃；
        }

        byte b1 = gt.getLast1Card();

        logger.info("msg.card_value={} b1={}",
                Integer.toHexString(msg.getCardValue()),Integer.toHexString(b1));

        if(b1 == 0)
            return;

        //设置吃的一组牌，客户端好表现
        msg.opValue=b1;
        //msg.player_table_pos = pl.getTablePos();
        msg.setCardValue(((gt.getLazarailloCard() << 8)|gt.getRiffraffCard()));
        this.sendMsgToTable(gt,msg,1);

        //
        gt.setCurrentCard((byte)0);
        gt.setCardOpPlayerIndex(-1);

        pl.setCardGrab(b1);
        MahjongCheckResult hu_xx =null;
        //


        hu_xx = mahjongProcessor.checkWin(b1, pl,gt);		//
        if(hu_xx!=null)//胡了
        {

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
        }
        else {
            waiting.gangOpPl = pl;
            waiting.playerTableIndex = pl.getTablePos();
            waiting.addFanResult(GameConstant.MAHJONG_HU_CODE_GANG_SHANG_PAO);
            waiting.opertaion = GameConstant.MAHJONG_OPERTAION_CHU;
            gt.setWaitingPlayerOperate(waiting);
        }
        player_chu_notify(gt, true, true);
    }



    private void printPlayerGoldNum(GameTable gt )
    {
        for(Player px :gt.getPlayers()){
            //获取杠分，去掉杠上炮玩家吃碰杠牌数组里最后一组牌，最后一组牌就是他杠的牌
            for (CardDown cd : px.getCardsDown()) {

                if (cd.type == GameConstant.MAHJONG_OPERTAION_MING_GANG ||cd.type == GameConstant.MAHJONG_OPERTAION_BU_GANG) {
                    Player chu = cd.chuPlayer;

                    logger.info("####### printPlayerGoldNum, px.getplayerindex = {}, px.getWinLoseGoldNum = {}," +
                                    " cd.getPlayerIndex = {}, chu.getWinLoseGoldNum = {}, cd.type = {}",
                            px.getPlayerIndex(),px.getWinLoseGoldNum(),chu.getPlayerIndex(),chu.getWinLoseGoldNum(),cd.type);
                }
            }
        }
    }


    private void calculate_player_max_fan(GameTable gt,Player pl)
    {
        pl.setMaxFanNum(0);
        List<Byte>cds=new ArrayList<Byte>();
        List<Byte>cds_inhand=pl.getCardsInHand();
        XiantaoMahjongProcessor processor = (XiantaoMahjongProcessor)mahjongProcessor;
        for(int i=0;i<cds_inhand.size();i++)
        {
            int b=cds_inhand.get(i);

            //
            processor.add_card_unique(cds,b);
            processor.add_card_unique(cds,b-1);
            processor.add_card_unique(cds,b+1);
        }
        //
        String hudesc="";
        for(int i=0;i<cds.size();i++)
        {
            byte b=cds.get(i);
            MahjongCheckResult hu=processor.checkWin(b, pl, gt);
            if(hu!=null)
            {
                hu.fanResult |=GameConstant.MAHJONG_HU_CODE_ZI_MO;// 按自摸来查叫
                MahjongCheckResult result=processor.getPlayerFanResult(pl,null,b,hu.fanResult,gt);
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
        logger.info("player id:{}, max fan: {}", pl.getPlayerIndex(), pl.getMaxFanNum());
    }

    @Override
    protected void check_next_player_op(GameTable gt,byte card,Player opPlayer,int checkOps)
    {
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
                    msg.setPengCardValue(result.getPengCardValue());
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
                        msg.setPengCardValue(pengresult.getPengCardValue());
                        result.setPengCardValue(msg.getPengCardValue());
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
                                msg.setChiCardValue(chi_result.getChiCardValue());
                                result.setChiCardValue(chi_result.getChiCardValue());//后面读取的是result的值

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
                    msg.setPengCardValue(result.getPengCardValue());

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
                            msg.setChiCardValue(chi_result.getChiCardValue());
                            result.setChiCardValue(chi_result.getChiCardValue());
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
            msg.setChiCardValue(result.getChiCardValue());
            msg.setPengCardValue(result.getPengCardValue());
            msg.setPlayerTablePos(result.playerTableIndex);
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
            player_mo(gt);
        }
    }




    @Override
    protected void game_over_hu(GameTable gt,boolean isLiuJu)
    {
        //不是流局
        if(isLiuJu==false)
        {
            gt.setLiuJuNoPlayerHu(false);
            //
            game_over_update_vip(gt, false);
        }
        else
        {
            gt.setLiuJuNoPlayerHu(true);

            chajiao(gt);
        }
        //合并计算提交数据库
        win_lose_money_submit(gt);
        //
        gt.setState(GameConstant.GAME_TABLE_STATE_SHOW_GAME_OVER_SCREEN);

        //
        long ctt=DateUtil.getCurrentUtilDate().getTime();
        gt.setHandEndTime(ctt);
    }

    @Override
    protected void game_over_update_vip(GameTable gt, boolean isLiuJu)
    {
        List<Player> plist=gt.getPlayers();
        Player pl = null;
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
                pl = px.getHuPaoPL();
                px.setWinCount(px.getWinCount()+1);
            }
        }

        //流局的时候，不计算杠的分
        if( isLiuJu )
        {
            return;
        }
    }



    // 按顺序查询下玩家能否进行某个操作
    // chuCardPlayerTableIndex为出牌玩家的位置，一个人出牌，引发多人吃，每次检测到了出牌那个位置，肯定要停止了，不能无限循环
    @Override
    protected MahjongCheckResult table_loop_check(GameTable gt, byte card, Player opPlayer, int checkOp,
                                                  int chuCardPlayerTableIndex) {
        MahjongCheckResult code = null;

        List<Player> plist = gt.getPlayers();
        int pl_index = opPlayer.getTablePos() + 1;
        if (pl_index >= plist.size())
            pl_index = 0;
        //
        //

        for (int i = 0; i < plist.size() - 1; i++) {
            Player pl = plist.get(pl_index);

            pl.setCancelHu(false);
            if (pl.getTablePos() == chuCardPlayerTableIndex)
                break;

            //

            // 已经听牌，看看能胡不
            if (checkOp == com.szxx.constant.GameConstant.MAHJONG_OPERTAION_HU) {
                // 先检测卡胡
                code = mahjongProcessor.checkWin(card, pl, gt);
                if (code != null) {
                    int waitFan = getWaitFan(gt);
                    code.addFanResult(waitFan);
                    if (code.fanResult == com.szxx.constant.GameConstant.MAHJONG_HU_CODE_YOU_XIAO_JI) {
                        code = null;
                    }
                    if ((gt.getVipRule() & com.szxx.constant.GameConstant.GAME_PLAY_RULE_CHUXIONG) != 0
                            && (gt.getVipRule() & com.szxx.constant.GameConstant.GAME_PLAY_RULE_DIANPAO) != 0) {
                        if (code.fanResult == 0) {
                            code = null;
                        }
                    }
                }
            }

            // 杠
            if (checkOp == com.szxx.constant.GameConstant.MAHJONG_OPERTAION_MING_GANG) {
                if (((gt.getVipRule() & com.szxx.constant.GameConstant.GAME_PLAY_RULE_CHUXIONG) == 0)
                        && ((gt.getVipRule() & com.szxx.constant.GameConstant.GAME_PLAY_RULE_YUXI) == 0)) {
                    code = mahjongProcessor.checkGang(gt, card, pl, false);
                }
            }

            // 碰
            if (checkOp == com.szxx.constant.GameConstant.MAHJONG_OPERTAION_PENG)
                code = mahjongProcessor.check_peng(gt, card, pl);

            //
            // 可以操作
            if (code != null) {
                // 所有操作只有一个玩家有可能进行
                break;
            }
            //
            pl_index++;
            if (pl_index >= plist.size())
                pl_index = 0;
        }

        //
        return code;
    }



    private void put_not_hu_player(GameTable gt,CardDown down,Player gangPl)
    {
        down.beGangPlayerList.clear();

        //
        List<Player> plist=gt.getPlayers();



        for(int i=0;i<plist.size();i++)
        {
            Player pl=plist.get(i);
            if(pl.isWin())
                continue;
            if(pl.getPlayerID().equals(gangPl.getPlayerID()))
                continue;
            down.beGangPlayerList.add(pl);

        }
    }

    private boolean checkPlayerTing(Player pl,GameTable gt)
    {
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
}
