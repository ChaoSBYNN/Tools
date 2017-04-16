package com.szxx.mahjong.xiantao.mahjong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.szxx.mahjong.xiantao.constant.GameConstant;
import com.szxx.domain.*;
import com.szxx.processor.mahjong.MahjongProcessor;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Component;

import static com.szxx.constant.GameConstant.MAHJONG_CODE_COLOR_TIAO;
import static com.szxx.constant.GameConstant.MAHJONG_CODE_COLOR_TONG;
import static com.szxx.constant.GameConstant.MAHJONG_CODE_COLOR_WAN;
import static com.szxx.constant.GameConstant.MAHJONG_CODE_COLOR_ZI;
import static com.szxx.constant.GameConstant.MAHJONG_HU_CODE_QINGYISE;
import static com.szxx.constant.GameConstant.MAHJONG_TING_CODE_CHENG_PAI;
import static com.szxx.constant.GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI;

/**
 * Description:
 * 仙桃麻将进程
 *
 * @author chris
 */
@SuppressWarnings("ALL")
@Component
public class XiantaoMahjongProcessor extends MahjongProcessor {

    private Logger logger = Logger.getLogger(XiantaoMahjongProcessor.class);

    public XiantaoMahjongProcessor() {
        this.allowLianPeng = false;
    }

    //CHAOS_ZHANG xiantao rule begin
    //CHAOS_ZHANG remove jiang_yi_se qing_yi_se peng_peng_hu 7_xiao_dui
    //CHAOS_ZHANG function guide:
    //CHAOS_ZHANG 1.check_chi 检测吃
    //CHAOS_ZHANG 2.check_peng 检测碰
    //CHAOS_ZHANG 3.check_gang_without_hun 检测杠 不包括 混子牌
    //CHAOS_ZHANG 4.checkWin 检测胡
    //CHAOS_ZHANG 5.1 real_check_qianjiang_hu 潜江胡法规则
    //CHAOS_ZHANG 5.2 real_check_xiantao_hu 仙桃胡法规则
    //CHAOS_ZHANG 6.isquanqiuren 仙桃全求人
    //CHAOS_ZHANG 7.remove_one 移除某花色点数牌 并返回该牌信息


    /**
     * 函数：吃
     * 潜江、仙桃没有吃
     *
     * @param gt 牌局
     * @param card 桌上打出牌
     * @param pl 玩家
     * @return
     */
    @Override
    public MahjongCheckResult check_chi(GameTable gt, byte card, Player pl){
        return null;
    }

    /**
     * 函数：碰
     * 潜江、仙桃玩法
     * 痞子牌、癞子牌不能碰
     * 痞子牌在杠里判断
     *
     * @param gt 牌局
     * @param card 桌上打出牌
     * @param pl 玩家
     * @return
     */
    @Override
    public MahjongCheckResult check_peng(GameTable gt, byte card, Player pl) {

        if(pl.isWin())
            return null;

        //获取牌局中痞子牌、癞子牌
        byte riffraffCard = gt.getRiffraffCard();
        byte lazarailloCard = gt.getLazarailloCard();

        //痞子牌、癞子牌不能碰
        //痞子牌在杠里判断
        if (card == riffraffCard || card == lazarailloCard) {
            return null;
        }

        int cd = card & 0xff;
        MahjongCheckResult result = null;

        //计算手牌数目
        int same_card_num = pl.getXCardNumInHand(card);

        //开放规则 如有3张手牌允许碰操作 留一张 可做其他操作
        if (same_card_num >= 2) {
            result = new MahjongCheckResult();
            result.targetCard = card;
            result.pengCardValue = cd | (cd << 8);
            result.playerTableIndex = pl.getTablePos();
            result.opertaion = GameConstant.MAHJONG_OPERTAION_PENG;
        }

        return result;

    }

    /**
     * 函数：检测杠
     *
     * @param gt 牌局
     * @param card 将要被操作的牌
     * @param pl 玩家
     * @param isMyCard 是否是本人的牌
     * @return
     */
    @Override
    public MahjongCheckResult check_gang_without_hun(GameTable gt, byte card, Player pl, boolean isMyCard) {

        //获取牌局中痞子牌、癞子牌
        byte riffraffCard = gt.getRiffraffCard();
        byte lazarailloCard = gt.getLazarailloCard();

        MahjongCheckResult res = new MahjongCheckResult();

        res.targetCard = card;
        res.pengCardValue = 0;
        res.playerTableIndex = pl.getTablePos();

        List<Byte> handList = new ArrayList<Byte>();
        for (int i = 0; i < pl.getCardsInHand().size(); i++) {
            byte b = pl.getCardsInHand().get(i);
            if (handList.contains(b))
                continue;

            handList.add(b);
        }

        if (isMyCard) {
            if (card > 0) {
                //刚摸进来的牌也要检测
                if (!handList.contains(card)) {
                    handList.add(card);
                }
            }
        }

        List<CardDown> cardGangs = res.gangList;
        if (isMyCard) {
            for (int k = 0; k < handList.size(); k++) {
                byte gg = handList.get(k);
                int num = pl.getXCardNumInHand(gg);
                if (gg == lazarailloCard) {
                    continue;
                }

                if (gg == card) {
                    num++;
                }

                if (num == 4) {
                    CardDown cardown = new CardDown();
                    cardown.type = GameConstant.MAHJONG_OPERTAION_AN_GANG;
                    cardown.cardValue = (gg << 24) | (gg << 16) | (gg << 8) | gg;
                    cardGangs.add(cardown);
                } else if (num == 1) {
                    //潜江麻将,摸了牌之后才能补杠，其他时间不能补杠
                    if (pl.hasPeng(gg)&& gg==card) {
                        CardDown bucardown = new CardDown();
                        bucardown.type = GameConstant.MAHJONG_OPERTAION_BU_GANG;
                        bucardown.cardValue = (gg << 24) | (gg << 16) | (gg << 8) | gg;
                        cardGangs.add(bucardown);
                    }
                }
                if (num == 3 && gg == riffraffCard) {
                    //潜江玩法，如果3个痞子可以亮牌,亮牌加入到杠中一起提示
                    CardDown liangpai = new CardDown();
                    //CHAOS_ZHANG add RIFFRAFF_AN_GANG
                    liangpai.type = GameConstant.MAHJONG_OPERTAION_AN_GANG;
                    liangpai.cardValue = (riffraffCard << 16) | (riffraffCard << 8) | riffraffCard;
                    cardGangs.add(liangpai);
                }
            }

        } else {
            int num = pl.getXCardNumInHand(card);
            if (num == 3) {
                CardDown cardown = new CardDown();
                cardown.type = GameConstant.MAHJONG_OPERTAION_MING_GANG;
                cardown.cardValue = (card << 16) | (card << 8) | card;
                cardGangs.add(cardown);
            }
            //潜江玩法，如果3个痞子可以亮牌,亮牌加入到杠中一起提示
            int piziNum = pl.getXCardNumInHand(riffraffCard);
            if (piziNum == 2 && riffraffCard == card) {
                CardDown liangpai = new CardDown();
                //CHAOS_ZHANG add RIFFRAFF_MING_GANG
                liangpai.type = GameConstant.MAHJONG_OPERTAION_MING_GANG;
                liangpai.cardValue = (riffraffCard << 16) | (riffraffCard << 8) | riffraffCard;
                cardGangs.add(liangpai);
            }
        }

        if (cardGangs.size() > 0) {
            res.opertaion = GameConstant.MAHJONG_OPERTAION_GANG;
            return res;
        }

        return null;
    }

    /**
     * 函数：胡
     *
     * @param card 将要被操作的牌
     * @param pl 玩家
     * @param gt 牌局
     * @return
     */
    @Override
    public MahjongCheckResult checkWin(byte card, Player pl,GameTable gt) {
        if(pl.isCancelHu()) {
            //必须过胡
            return null;
        }

        if (pl.isWin()) {
            return null;
        }

        byte lazarailloCard = gt.getLazarailloCard();
        int laziNum = 0;
        //card是别人出的牌:硬胡(癞子归位、无癞子、做将不算归位)才允许点炮
        if (pl.getCardGrab() == 0) {
            //别人出牌，硬胡才能胡牌。癞子归位算硬胡，做将不算归位
            //别人打出癞子不能胡牌
            //if (card == laizi) {
            //    return null;
            //}
            //硬胡在real_check_hu判断
        } else {
            //card是自己抓的牌
            pl.addCardInHand(card);
            laziNum = pl.getXCardNumInHand(lazarailloCard);//获取玩家手上的癞子数,摸上来的牌也要算
            pl.removeCardInHand(card);
        }

        //CHAOS_ZHANG 仙桃麻将:【规则细分a: 一癞到底】：手中只能有一张癞子或没有的情况下，达到胡牌牌型才能胡牌。（一癞）
        if (gt.getVipRule() == GameConstant.GAME_PLAY_RULE_XIANTAO_YILAIDAODI && laziNum > 1) {
            return null;
        }
        //CHAOS_ZHANG 仙桃麻将:【规则细分c: 硬晃】：和癞子有关的东西都没有（没有癞子，没有朝天）
        MahjongCheckResult result = null;
        if(gt.getVipRule() == GameConstant.GAME_PLAY_RULE_XIANTAO_YINGHUANG){
            result = real_check_xiantao_hu_without_hun(card, pl, gt);
        }else{
            //CHAOS_ZHANG 仙桃麻将:【规则细分b: 干瞪眼】：手中无论有多少癞子，只要能达到胡牌牌型即可胡牌。（多癞）
            result = real_check_xiantao_hu(card, pl, gt);
        }

        int index = gt.getRoomID();

        return result;

    }

    //CHAOS_ZHANG doCheckWin...real_check_xiantao_hu_without_hun...real_check_xiantao_hu
    @Override
    public MahjongCheckResult doCheckWin(byte card, Player pl, GameTable gt) {

        // 把牌先放手里，有序,后面再删掉
        if (card != 0) {
            pl.addCardInHand(card);
        }

        MahjongCheckResult huresult = new MahjongCheckResult();
        huresult.opertaion = GameConstant.MAHJONG_OPERTAION_HU;
        huresult.playerTableIndex = pl.getTablePos();
        huresult.targetCard = card;


        byte hun_cd = gt.getLazarailloCard();

        boolean iszimo = (pl.getCardGrab() != 0);


        List<Byte> c1 = pl.getCardsInHand();

        int hun_num = 0;

        byte hun2=0;

        do {

            CardPatternCheckResult wan =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_WAN,hun_cd,hun2,hun_num);
            if(wan==null)
                break;// 当前万子不成牌
            hun_num -=wan.hunUsed;

            CardPatternCheckResult tiao = x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_TIAO,hun_cd,hun2,hun_num);
            if(tiao==null)
                break;// 当前条子听不了
            hun_num -=tiao.hunUsed;

            //
            CardPatternCheckResult tong =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_TONG,hun_cd,hun2,hun_num);
            if(tong==null)
                break;// 当前筒子听不了
            hun_num -=tong.hunUsed;

            //
            CardPatternCheckResult zi =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_ZI,hun_cd,hun2,hun_num);
            if(zi==null)
                break;// 字牌听不了
            hun_num -=zi.hunUsed;


            int cheng_pai_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tong.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (zi.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;

            //
            //int dui_zi=0;
            int duizi_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                duizi_num++;
                //dui_zi=wan.duiZi;
            }
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                duizi_num++;
                //dui_zi=tiao.duiZi;
            }
            if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                duizi_num++;
                //dui_zi=tong.duiZi;
            }
            if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                duizi_num++;
            }

            //有太多对子，如果还有混牌，其中一对变刻子，
            while(duizi_num>1&& hun_num>0)
            {
                if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    zi.setDuiZiValue(0);
                    zi.setKeziNum(zi.getKeziNum()+1);
                    zi.hunUsed++;

                    continue;
                }

                if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
                if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
                if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
            }

            int total_num=wan.getKeziNum()+tiao.getKeziNum()+tong.getKeziNum();
            //2张胡别人不算一坎
            if (pl.getXCardNumInHand(card) == 3) {
                wan.removeKe(card);
                tiao.removeKe(card);
                tong.removeKe(card);
            }
            int kezi=wan.getKeziNum()+tiao.getKeziNum()+tong.getKeziNum();
            if (cheng_pai_num >= 3 && duizi_num == 1) {
                int vip_rule = gt.getVipRule();
                int youlaizi = 0;
                int value = 0;
                int laiziNum = pl.getXCardNumInHand(hun_cd);
                if ((wan.hunUsed > 0 || wan.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x0)) {
                    value = wan.getXiaojiShunzi(hun_cd);
                }
                if ((tiao.hunUsed > 0 || tiao.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x10)) {
                    value = tiao.getXiaojiShunzi(hun_cd);
                }
                if ((tong.hunUsed > 0 || tong.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x20)) {
                    value = tong.getXiaojiShunzi(hun_cd);
                }

                boolean hasHunDuizi = false;
                boolean hasHunKezi = false;
                if (wan.findDuiZi(hun_cd) || tiao.findDuiZi(hun_cd) || tong.findDuiZi(hun_cd) || zi.findDuiZi(hun_cd)) {
                    hasHunDuizi = true;
                }
                if (wan.find_ke_zi(hun_cd) || tiao.find_ke_zi(hun_cd) || tong.find_ke_zi(hun_cd) || zi.find_ke_zi(hun_cd)) {
                    hasHunKezi = true;
                }

                int laizileft = 0;
                if (hasHunDuizi) {
                    laizileft = laiziNum - 2 - value;
                } else if (hasHunKezi) {
                    laizileft = laiziNum - 3 - value;
                } else {
                    laizileft = laiziNum - value;
                }

                int hunUsed=wan.hunUsed + tiao.hunUsed + tong.hunUsed;
                if (pl.getCardGrab() == 0) {
                    //点炮
                    if(hunUsed >= 1 || hasHunDuizi ||hasHunKezi){//硬胡才允许点炮
                        //hunUsed >= 1表示不是硬胡,hasHunDuizi
                        pl.removeCardInHand(card);
                        return null;
                    }
                }else {
                    //自摸
                    if(hunUsed ==0 && !hasHunDuizi ){//硬胡才允许点炮
                        huresult.addFanResult(GameConstant.MAHJONG_HU_CODE_HEIMO);
                    }
                }

                if(hasHunDuizi){
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }

                if (laizileft > 0) {
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }

                if (zi.hunUsed > 0) {
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }

                //碰碰胡放到大三元和小三元后面计算

                huresult.keZiNum = kezi;
                huresult.totalKeZiNum = total_num;
                //双铺子：两个一样的顺，如345万 345万
                huresult.same2Shunzi = wan.hasTwoSameShunzi() + tiao.hasTwoSameShunzi() + tong.hasTwoSameShunzi();

                huresult.addFanResult(youlaizi);

                wan.clearAll();
                tiao.clearAll();
                tong.clearAll();

                pl.removeCardInHand(card);

                return huresult;
            } else {
                if (duizi_num > 1) {
                    //碰碰胡放到大三元和小三元后面计算
                    MahjongCheckResult ppdui = check_hu_peng_peng_with_hun(card, pl, gt, hun_cd);
                    if (ppdui != null) {
                        pl.removeCardInHand(card);
                        return ppdui;
                    }
                }
            }
            wan.clearAll();
            tiao.clearAll();
            tong.clearAll();
            //
        } while (false);

        // 临时牌，先移掉
        pl.removeCardInHand(card);
        return null;
    }

    private MahjongCheckResult real_check_xiantao_hu_without_hun(byte card, Player pl, GameTable gt) {
        // 把牌先放手里，有序,后面再删掉
        if(card != 0) {
            pl.addCardInHand(card);
        }

        MahjongCheckResult huresult =  new MahjongCheckResult();
        huresult.opertaion = GameConstant.MAHJONG_OPERTAION_HU;
        huresult.playerTableIndex = pl.getTablePos();
        huresult.targetCard = card;

        byte hun_cd = 0x70;		// 没有混子

        boolean iszimo = (pl.getCardGrab() != 0);
        if(iszimo){

        }


        //
        List<Byte> c1 = pl.getCardsInHand();
        List<CardDown> c2 = pl.getCardsDown();

        //有几张混牌
//		int hun_num=pl.getXCardNumInHand(hun_cd);
        int hun_num = 0;

        byte hun2=0;

        do{

            CardPatternCheckResult wan =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_WAN,hun_cd,hun2,hun_num);
            if(wan==null)
                break;// 当前万子不成牌
            hun_num -=wan.hunUsed;

            CardPatternCheckResult tiao = x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_TIAO,hun_cd,hun2,hun_num);
            if(tiao==null)
                break;// 当前条子听不了
            hun_num -=tiao.hunUsed;

            //
            CardPatternCheckResult tong =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_TONG,hun_cd,hun2,hun_num);
            if(tong==null)
                break;// 当前筒子听不了
            hun_num -=tong.hunUsed;

            //
            CardPatternCheckResult zi =x_hu_check(c1,GameConstant.MAHJONG_CODE_COLOR_ZI,hun_cd,hun2,hun_num);
            if(zi==null)
                break;// 字牌听不了
            hun_num -=zi.hunUsed;

            int cheng_pai_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tong.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (zi.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;

            //
            //int dui_zi=0;
            int duizi_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=wan.duiZi;
            }
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=tiao.duiZi;
            }
            if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=tong.duiZi;
            }
            if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
            }

            //有太多对子，如果还有混牌，其中一对变刻子，
            while(duizi_num>1&& hun_num>0)
            {
                if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    zi.setDuiZiValue(0);
                    zi.setKeziNum(zi.getKeziNum()+1);
                    zi.hunUsed++;

                    continue;
                }

                if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
                if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
                if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    continue;
                }
            }

            int total_num=wan.getKeziNum()+tiao.getKeziNum()+tong.getKeziNum();
            //2张胡别人不算一坎
            if(pl.getXCardNumInHand(card)==3)
            {
                wan.removeKe(card);
                tiao.removeKe(card);
                tong.removeKe(card);
            }
            int kezi=wan.getKeziNum()+tiao.getKeziNum()+tong.getKeziNum();

            //
            if (cheng_pai_num >= 3 && duizi_num == 1 )
            {
                //碰碰胡放到大三元和小三元后面计算
                MahjongCheckResult ppdui= check_hu_peng_peng(card,pl,gt);
                if(ppdui!=null)
                {
                    check_qingyseorhun(card,pl,ppdui,hun_cd);
                    pl.removeCardInHand(card);
                    if (tong.findDuiZi(card)) {
                        huresult.isDandiao = true;
                    }
                    return ppdui;
                }
                MahjongCheckResult res = new MahjongCheckResult();
                res.opertaion = GameConstant.MAHJONG_OPERTAION_HU;
                res.playerTableIndex = pl.getTablePos();
                res.targetCard = card;
                //
                res.keZiNum=kezi;
                res.totalKeZiNum=total_num;
                //双铺子：两个一样的顺，如345万 345万
                res.same2Shunzi=wan.hasTwoSameShunzi()+tiao.hasTwoSameShunzi()+tong.hasTwoSameShunzi();

                if (tong.findDuiZi(card)) {
                    res.isDandiao = true;
                }

                wan.clearAll();
                tiao.clearAll();
                tong.clearAll();

                // 临时牌，先移掉
                pl.removeCardInHand(card);
                //
                return res;
            }else
            {
                if(duizi_num>1)
                {
                    //碰碰胡放到大三元和小三元后面计算
                    MahjongCheckResult ppdui= check_hu_peng_peng(card,pl,gt);
                    if(ppdui!=null)
                    {
                        pl.removeCardInHand(card);
                        return ppdui;
                    }
                }
            }
            wan.clearAll();
            tiao.clearAll();
            tong.clearAll();
        } while (false);

        // 临时牌，先移掉
        pl.removeCardInHand(card);
        return null;
    }

    /**
     * 函数：检测仙桃胡
     *
     * @param card 将要被操作的牌
     * @param pl 玩家
     * @param gt 牌局
     * @return
     */
    public MahjongCheckResult real_check_xiantao_hu(byte card, Player pl, GameTable gt) {

        // 把牌先放手里，有序,后面再删掉
        if (card != 0) {
            pl.addCardInHand(card);
        }

        MahjongCheckResult huresult = new MahjongCheckResult();
        huresult.opertaion = GameConstant.MAHJONG_OPERTAION_HU;
        huresult.playerTableIndex = pl.getTablePos();
        huresult.targetCard = card;

        byte hun_cd = gt.getLazarailloCard();

        boolean iszimo = (pl.getCardGrab() != 0);



        //CHAOS_ZHANG 已有打出癞子牌，必须自摸才能胡
        //CHAOS_ZHANG 手牌中有癞子不能胡放铳
        boolean isLazarailloCardInDownCards = gt.getVisibleCardNum(hun_cd) > 0;
        boolean isLazarailloCardInHand = pl.hasCardInHand(hun_cd);

        if(isLazarailloCardInDownCards && isLazarailloCardInHand && !iszimo){
            pl.removeCardInHand(card);
            return null;
        }

        //CHAOS_ZHANG 见字胡
        boolean isSeeWord = isSeeWord(card, pl, huresult, hun_cd);

        if(isSeeWord){
            pl.removeCardInHand(card);
            return huresult;
        }



        List<Byte> handCard = pl.getCardsInHand();

        //有几张混牌
        int hun_num = 0;

        byte hun2=0;

        do {

            CardPatternCheckResult wan =x_hu_check(handCard,GameConstant.MAHJONG_CODE_COLOR_WAN,hun_cd,hun2,hun_num);
            if(wan==null)
                break;// 当前万子不成牌
            hun_num -=wan.hunUsed;

            CardPatternCheckResult tiao = x_hu_check(handCard,GameConstant.MAHJONG_CODE_COLOR_TIAO,hun_cd,hun2,hun_num);
            if(tiao==null)
                break;// 当前条子听不了
            hun_num -=tiao.hunUsed;

            //
            CardPatternCheckResult tong =x_hu_check(handCard,GameConstant.MAHJONG_CODE_COLOR_TONG,hun_cd,hun2,hun_num);
            if(tong==null)
                break;// 当前筒子听不了
            hun_num -=tong.hunUsed;

            //
            CardPatternCheckResult zi =x_hu_check(handCard,GameConstant.MAHJONG_CODE_COLOR_ZI,hun_cd,hun2,hun_num);
            if(zi==null)
                break;// 字牌听不了
            hun_num -=zi.hunUsed;

            int cheng_pai_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (tong.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;
            if (zi.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
                cheng_pai_num++;

            //
            //int dui_zi=0;
            int duizi_num = 0;
            if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=wan.duiZi;
            }
            if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=tiao.duiZi;
            }
            if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
                //dui_zi=tong.duiZi;
            }
            if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI){
                duizi_num++;
            }

            //有太多对子，如果还有混牌，其中一对变刻子，
            while (duizi_num > 1 && hun_num > 0) {
                if (zi.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;
                    zi.setDuiZiValue(0);
                    zi.setKeziNum(zi.getKeziNum()+1);
                    zi.hunUsed++;

                    continue;
                }

                if (wan.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;

                    wan.setDuiZiValue(0);
                    wan.setKeziNum(zi.getKeziNum()+1);
                    wan.hunUsed++;
                    continue;
                }
                if (tong.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;

                    tong.setDuiZiValue(0);
                    tong.setKeziNum(zi.getKeziNum()+1);
                    tong.hunUsed++;
                    continue;
                }
                if (tiao.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI) {
                    duizi_num--;
                    cheng_pai_num++;
                    hun_num--;

                    tiao.setDuiZiValue(0);
                    tiao.setKeziNum(zi.getKeziNum()+1);
                    tiao.hunUsed++;
                    continue;
                }
            }

            int total_num = wan.getKeziNum() + tiao.getKeziNum() + tong.getKeziNum();

            //2张胡别人不算一坎
            if (pl.getXCardNumInHand(card) == 3) {
                wan.removeKe(card);
                tiao.removeKe(card);
                tong.removeKe(card);
            }

            int kezi = wan.getKeziNum() + tiao.getKeziNum() + tong.getKeziNum();

            if (cheng_pai_num >= 3 && duizi_num == 1) {
                int vip_rule = gt.getVipRule();
                int youlaizi = 0;
                int value = 0;
                int laiziNum = pl.getXCardNumInHand(hun_cd);
                //chaos_zhang useHuncardAsNormalNum => getHunUsed()
                //chaos_zhang getXiaojiShunziNum(hun_cd, vip_rule) => getXiaojiShunzi(hun_cd)
                if ((wan.hunUsed > 0 || wan.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x0)) {
                    value = wan.getXiaojiShunzi(hun_cd);
                }
                if ((tiao.hunUsed > 0 || tiao.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x10)) {
                    value = tiao.getXiaojiShunzi(hun_cd);
                }
                if ((tong.hunUsed > 0 || tong.getHunUsed() > 0) && ((hun_cd & 0xf0) == 0x20)) {
                    value = tong.getXiaojiShunzi(hun_cd);
                }

                boolean hasHunDuizi = false;
                boolean hasHunKezi = false;
                if (wan.findDuiZi(hun_cd) || tiao.findDuiZi(hun_cd) || tong.findDuiZi(hun_cd) || zi.findDuiZi(hun_cd)) {
                    hasHunDuizi = true;
                }
                if (wan.find_ke_zi(hun_cd) || tiao.find_ke_zi(hun_cd) || tong.find_ke_zi(hun_cd) || zi.find_ke_zi(hun_cd)) {
                    hasHunKezi = true;
                }

                MahjongCheckResult res = new MahjongCheckResult();

                int hunUsed=wan.hunUsed + tiao.hunUsed + tong.hunUsed;
                if (pl.getCardGrab() == 0) {
                    //点炮
                    if(hunUsed >= 1 || hasHunDuizi ||hasHunKezi){//硬胡才允许点炮
                        //hunUsed >= 1表示不是硬胡,hasHunDuizi
                        pl.removeCardInHand(card);
                        return null;
                    }
                }else {
                    //自摸
                    if(hunUsed ==0 && !hasHunDuizi ){//硬胡才允许点炮
                        res.addFanResult(GameConstant.MAHJONG_HU_CODE_HEIMO);
                    }
                }

                //CHAOS_ZHANG 硬晃没有癞子操作

                int laizileft = 0;
                if (hasHunDuizi) {
                    laizileft = laiziNum - 2 - value;
                } else if (hasHunKezi) {
                    laizileft = laiziNum - 3 - value;
                } else {
                    laizileft = laiziNum - value;
                }

                if(hasHunDuizi){
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }

                if (laizileft > 0) {
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }

                if (zi.hunUsed > 0) {
                    youlaizi = GameConstant.MAHJONG_HU_CODE_YOU_LAI_ZI;
                }



                res.opertaion = GameConstant.MAHJONG_OPERTAION_HU;
                res.playerTableIndex = pl.getTablePos();
                res.targetCard = card;
                //
                res.keZiNum = kezi;
                res.totalKeZiNum = total_num;
                //双铺子：两个一样的顺，如345万 345万
                res.same2Shunzi = wan.hasTwoSameShunzi() + tiao.hasTwoSameShunzi() + tong.hasTwoSameShunzi();

                res.addFanResult(youlaizi);

                wan.clearAll();
                tiao.clearAll();
                tong.clearAll();

                pl.removeCardInHand(card);

                return res;
            }
            wan.clearAll();
            tiao.clearAll();
            tong.clearAll();
            //
        } while (false);

        // 临时牌，先移掉
        pl.removeCardInHand(card);
        return null;
    }

    /**
     * 函数：检测见字胡
     * 手上的牌全成对刻，剩单癞子，则胡所有牌
     * 1. 如全成刻字 -> true
     * 2. 如单吊一张 -> true
     * 3. 如单张大于1张 -> false
     *
     * @param card 将要被操作的牌
     * @param pl 玩家
     * @param res 结果
     * @param laizi 癞子牌
     * @param hu_type 胡牌规则
     * @return
     */
    public boolean isSeeWord(byte card,Player pl,MahjongCheckResult res, Byte laizi) {

        //CHAOS_ZHANG 癞子数
        int lazaraiollCardNum = pl.getCardXNumAll(laizi);
        //CHAOS_ZHANG 见字胡癞子数为1
        if(lazaraiollCardNum != 1) {
            return false;
        }

        //CHAOS_ZHANG 手牌判断非单数
        List<Byte> cards = pl.getCardsInHand();

        HashMap<String,String> map = new HashMap<String, String>();

        boolean isNotThree = true;

        for (Byte checkCard : cards) {
            //CHAOS_ZHANG 不计癞子
            if (checkCard.equals(laizi)) {
                continue;
            }

            //CHAOS_ZHANG 全刻见字胡 仙桃麻将没有吃 不存在顺子 某花色牌大于2 非碰即杠
            if (pl.getCardXNumAll(checkCard) < 3) {
                //CHAOS_ZHANG 单吊个数大于1不构成见字胡
                if (map.containsKey("checkDanDiao")) {
                    return false;
                } else {
                    map.put("checkDanDiao", "true");
                }

            }

        }

        return isNotThree;

    }

    /**
     * 函数：移除某张牌
     *
     * @param array 牌组
     * @param c 某张牌
     * @return
     */
    private boolean  remove_one(List<Byte> array ,byte c) {
        for(int i=0;i<array.size();i++)
        {
            Byte b=array.get(i);
            if(b==c)
            {
                array.remove(i);
                return true;
            }
        }

        return false;
    }


    //chaos_zhang xiantao rule end

    public MahjongCheckResult check_ting(byte card, Player pl,GameTable gt) {
        MahjongCheckResult res=do_check_ting(card, pl,gt) ;

        return res;
    }

    private void set_ting_type(CardPatternCheckResult ck) {
        if (ck.getUncheckedCards().size() > 0)
            return;
        //
        if (ck.getDuiZiValue() == 0 && ck.danDiao == 0 && ck.shunZi == 0)// 如果之前没有听牌，说明已经成牌
        {
            ck.result = GameConstant.MAHJONG_TING_CODE_CHENG_PAI;
        } else if (ck.danDiao > 0 ) {
            ck.result = GameConstant.MAHJONG_TING_CODE_DAN_DIAO;
        } else if (ck.shunZi > 0 ) {
            ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI;
        }
        //
        else if (ck.getDuiziNum() == 1) {
            ck.result = GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI;
        } else if (ck.getDuiziNum() == 2) {
            ck.result = GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI;
        }
    }

    private boolean could_ting_check(CardPatternCheckResult ck) {
        int num_left = ck.getUncheckedCards().size();
        if (ck.danDiao > 0 && num_left > 0) {
            return false;
        }


        //单调同时听顺子的情况不能成牌
        if(ck.danDiao>0 && ck.isTingCardShun){
            return false;
        }

        if(ck.danDiao>0 && ck.getDuiZiValue()>0){
            return false;
        }

        // 如果只有一张牌
        if (num_left == 0) {
            set_ting_type(ck);
            return true;
        } else if (num_left == 1) {
            if (ck.getDuiZiValue() == 0 && ck.danDiao == 0 && ck.shunZi == 0) {
                // 没有听其他牌，那就单调这张牌，可能听
                ck.danDiao = ck.getUncheckedCards().remove(0);
                ck.result = GameConstant.MAHJONG_TING_CODE_DAN_DIAO;
                return true;
            } else {
                // 多张单牌，听不了
                return false;
            }
        } else if (num_left == 2)// 如果只有两张牌
        {
            byte c1 = ck.getUncheckedCards().get(0);
            byte c2 = ck.getUncheckedCards().get(1);
            if (c1 == c2) {
                if (ck.getDuiziNum() == 0// 是否可以再添加一个对子
                        || (ck.getDuiziNum() == 1
                        && ck.danDiao == 0 && ck.shunZi == 0)) {
                    ck.getUncheckedCards().remove(0);
                    ck.getUncheckedCards().remove(0);
                    //
                    ck.addDuizi(c1);
                    //
                    set_ting_type(ck);
                    //
                    return true;
                } else {
                    return false;// 听不了，3个对子
                }
            } else if (ck.getDuiziNum() < 2 && ck.danDiao == 0
                    && ck.shunZi == 0)// 没有单调，没有听顺子，就可能听这2牌的,
            {
//                if(c1>=0x31 && c1<=0x34)//东南西北
//                    return false;
//
//                if(c2>=0x31 && c2<=0x34)//东南西北
//                    return false;
//
//                if(c1==0x37 || c2==0x37)//白
//                    return false;
                //
                if (c1 == c2 - 1 || c1 == c2 + 1) {
                    ck.shunZi = c1 | (c2 << 8);
                    ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI;

                    return true;
                }
                if (c1 == c2 - 2 || c1 == c2 + 2) {
                    ck.shunZi = c1 | (c2 << 8);
                    ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI_KA_ZHANG;
                    return true;
                }
                //
                return false;// 2张不连续牌，听不了,或者有单调，又有2张不连续的
            }
            return false;// 至少2张单牌
        }
        //
        return false;// 不会到这里

    }

    // 这一组牌能听，就直接返回，至于听什么牌，能听多种组合，客户端暂时不需要展示，就不判断了
    public void ting_check(List<CardPatternCheckResult> outs, CardPatternCheckResult ck) {
        if (ck.getUncheckedCards().size() <= 2) {
            if (could_ting_check(ck)) {
                outs.add(ck);
            }
            return;
        }
        //

        //
        byte b1 = ck.getUncheckedCards().get(0);
        byte b2 = ck.getUncheckedCards().get(1);
        byte b3 = ck.getUncheckedCards().get(2);
        //

        int color=b1&GameConstant.MAHJONG_CODE_COLOR_MASK;
        //
        // 第一张牌单调,听牌
        if (ck.danDiao == 0) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);

            byte danDiao = ckx.getUncheckedCards().remove(0);
            ckx.danDiao = danDiao;
            //
            ting_check(outs, ckx);
        }
        // 第一张作为对子
        if (ck.danDiao == 0 && (b1 == b2)&& (ck.couldAddMoreDuizi()))
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            ckx.addDuizi(b1);
            //
            ting_check(outs, ckx);

        }
        // 第一张作为刻牌aaa,成牌
        if (b1 == b2 && b2 == b3) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            ckx.setKeziNum(ckx.getKeziNum()+1);

            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            ting_check(outs, ckx);

        }

        //
        //字牌不能组成顺子牌
        if(color!=GameConstant.MAHJONG_CODE_COLOR_ZI)
        {
            // 第一张作为顺牌abc，成牌
            if (ck.foundCard((byte) (b1 + 1)) && ck.foundCard((byte) (b1 + 2))) {
                CardPatternCheckResult ckx = new CardPatternCheckResult();
                ckx.copy(ck);
                ckx.shunZiNum++;// 加多一个顺子

                ckx.getUncheckedCards().remove(0);
                ckx.remove((byte) (b1 + 1));
                ckx.remove((byte) (b1 + 2));
                //
                int cc = b1 | ((b1 + 1) << 8) | ((b1 + 2) << 16);
                ckx.getCheckedCards().add(cc);
                //
                ting_check(outs, ckx);

            }

            //

            //卡顺
            // 就听第一张和某一张组成顺子，听顺子
            if (ck.getDuiziNum() != 2) {
                if (ck.danDiao == 0 && ck.shunZi == 0
                        && ck.foundCard((byte) (b1 + 1))) {
                    CardPatternCheckResult ckx = new CardPatternCheckResult();
                    ckx.copy(ck);
                    //
                    ckx.getUncheckedCards().remove(0);
                    ckx.remove((byte) (b1 + 1));
                    //
                    ckx.shunZi = b1 | ((b1 + 1) << 8);// 如果后面的面，检测都成牌，这就可能听顺子
                    //
                    ckx.isTingCardShun = true; // 标示了这个就不能再对对胡
                    ting_check(outs, ckx);
                }

            }


            // 就听第一张和某一张组成顺子，听卡顺子
            if (ck.getDuiziNum() != 2 ) {
                if (ck.danDiao == 0 && ck.shunZi == 0
                        && ck.foundCard((byte) (b1 + 2))) {
                    CardPatternCheckResult ckx = new CardPatternCheckResult();
                    ckx.copy(ck);
                    //
                    ckx.getUncheckedCards().remove(0);
                    ckx.remove((byte) (b1 + 2));
                    //
                    ckx.shunZi = b1 | ((b1 + 2) << 8);// 如果后面的面，检测都成牌，这就可能听卡顺子

                    ckx.isTingCardShun = true; // 标示了这个就不能再对对胡
                    //
                    ting_check(outs, ckx);
                }
            }
        }


    }

    //0不听，1听对倒，2单吊，3听顺子
    private int ting_check_one_by_one(byte removed_card,MahjongCheckResult res,Player pl,
                                      CardPatternCheckResult tiao_result,
                                      CardPatternCheckResult wan_result, CardPatternCheckResult tong_result, CardPatternCheckResult zi_result,GameTable gt) {
        if (tiao_result == null || wan_result == null || tong_result == null|| zi_result == null)
            return 0;

        int cheng_pai_num = 0;
        if (wan_result.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
            cheng_pai_num++;
        if (tiao_result.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
            cheng_pai_num++;
        if (tong_result.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
            cheng_pai_num++;
        if (zi_result.result == GameConstant.MAHJONG_TING_CODE_CHENG_PAI)
            cheng_pai_num++;




        // 3组牌都是听牌，听不了
        if (cheng_pai_num ==0)
            return 0;
        //
        int duizi_num = 0;
        if (wan_result.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI)
            duizi_num++;

        if (tiao_result.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI)
            duizi_num++;

        if (tong_result.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI)
            duizi_num++;

        if (zi_result.result == GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI)
            duizi_num++;


        //
        if (wan_result.result == GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI)
            duizi_num += 2;
        if (tiao_result.result == GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI)
            duizi_num += 2;
        if (tong_result.result == GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI)
            duizi_num += 2;
        if (zi_result.result == GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI)
            duizi_num += 2;



        int dandiao_num = 0;
        if (wan_result.result == GameConstant.MAHJONG_TING_CODE_DAN_DIAO)
            dandiao_num++;
        if (tiao_result.result == GameConstant.MAHJONG_TING_CODE_DAN_DIAO)
            dandiao_num++;
        if (tong_result.result == GameConstant.MAHJONG_TING_CODE_DAN_DIAO)
            dandiao_num++;
        if (zi_result.result == GameConstant.MAHJONG_TING_CODE_DAN_DIAO)
            dandiao_num++;


        // 听顺子，半城牌的，2张牌的顺子
        int ting_shunzi_num = 0;
        if (wan_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI|| wan_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI_KA_ZHANG) {
            ting_shunzi_num++;
            duizi_num += wan_result.getDuiziNum();
        }
        if (tiao_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI|| tiao_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI_KA_ZHANG) {
            ting_shunzi_num++;
            duizi_num += tiao_result.getDuiziNum();
        }

        if (tong_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI || tong_result.result == GameConstant.MAHJONG_TING_CODE_SHUN_ZI_KA_ZHANG) {
            ting_shunzi_num++;
            duizi_num += tong_result.getDuiziNum();
        }



        // 这个是听对子
        // 对子如果是0，或者超过2对，就听不了
        if (ting_shunzi_num == 0 && duizi_num >= 3)
            return 0;

        // 单调不能超过2
        if (dandiao_num >= 2)
            return 0;

        //手里有对子了，还听对子单调，听不了
        if (dandiao_num ==  1 && duizi_num>=1)
            return 0;
        // 听顺子不能超过2
        if (ting_shunzi_num >= 2)
            return 0;


        //
        // 2种成牌，一种听(单调)
        if (cheng_pai_num == 3&&   duizi_num == 0 && dandiao_num == 1)
        {

            return 2;

        }

        if (duizi_num == 1)
        {

            //这个基本不起作用，上面已经判断单调了
            if (dandiao_num > 1)
                return 0;

            //
            // 一组成牌，一组对子，另外一组听顺牌
            if (ting_shunzi_num == 1  )
            {
                return 3;

            }

            return 0;
        }// 两个对子
        else if (duizi_num == 2)
        {
            if (dandiao_num > 0)
                return 0;

            // 没有听顺子或者，单调
            if (ting_shunzi_num == 0 && dandiao_num == 0 )
            {

                return 1;

            }

            return 0;
        }

        return 0;
    }

    private int ting_check_final(MahjongCheckResult res,byte cdx,Player pl,
                                 List<CardPatternCheckResult> tiao_result,
                                 List<CardPatternCheckResult> wan_result,
                                 List<CardPatternCheckResult> tong_result,
                                 List<CardPatternCheckResult> zi_result,GameTable gt) {
        // System.out.println("听牌检测,条="+tiao_result.size()+",筒="+tong_result.size()+",万="+wan_result.size());
        //

        for (int i = 0; i < tiao_result.size(); i++)
        {
            CardPatternCheckResult tiao = tiao_result.get(i);
            //
            for (int j = 0; j < wan_result.size(); j++)
            {
                CardPatternCheckResult wan = wan_result.get(j);
                //
                for (int k = 0; k < tong_result.size(); k++)
                {
                    CardPatternCheckResult tong = tong_result.get(k);

                    for (int m = 0; m < zi_result.size(); m++)
                    {
                        CardPatternCheckResult zi = zi_result.get(m);
                        //
                        if (tiao.result != 0 && wan.result != 0 && tong.result != 0 && zi.result != 0)
                        {
                            int resx=ting_check_one_by_one(cdx,res,pl,tiao, wan, tong,zi,gt);
                            if(resx>0)
                            {
                                return resx;
                            }
                        }
                    }
                }
            }

        }


        return 0;
    }

    private int  get_num_one(List<Byte> array ,byte c) {
        int num=0;
        for(int i=0;i<array.size();i++)
        {
            Byte b=array.get(i);
            if(b==c)
            {
                num++;
            }
        }
        //
        return num;
    }

    public void add_card_unique(List<Byte>cds,int cd) {
        byte bb=(byte)(cd&0xff);
        int v=cd&0xf;
        if(v<=0 || v>9)
            return;
        //


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

    public MahjongCheckResult getPlayerFanResult(Player pl,Player pao_pl,byte moCard,int fanResult,GameTable gt) {
        //
        int fan_type=GameConstant.MAHJONG_HU_CODE_TING|GameConstant.MAHJONG_HU_CODE_WIN;
        //
        int fan=0; //底翻0翻
        //
        String desc="";
        //自摸
        if((fanResult & GameConstant.MAHJONG_HU_CODE_ZI_MO)==  GameConstant.MAHJONG_HU_CODE_ZI_MO)
        {
            fan_type |= GameConstant.MAHJONG_HU_CODE_ZI_MO;
            desc=" 自摸";
        }
        //
        if((fanResult & GameConstant.MAHJONG_HU_CODE_PENG_PENG_HU)==  GameConstant.MAHJONG_HU_CODE_PENG_PENG_HU)
        {
            fan += 1;// 碰碰胡
            fan_type |= GameConstant.MAHJONG_HU_CODE_PENG_PENG_HU;

            desc +="  碰碰胡×1";
        }

        //门清自摸加1分
        if((fanResult & GameConstant.MAHJONG_HU_CODE_LONG_QI_DUI)==  GameConstant.MAHJONG_HU_CODE_LONG_QI_DUI)
        {
            fan += 2;
            fan_type |= GameConstant.MAHJONG_HU_CODE_LONG_QI_DUI;

            desc +="  龙抓背×2";
        } else if((fanResult & GameConstant.MAHJONG_HU_CODE_QIXIAODUI)==  GameConstant.MAHJONG_HU_CODE_QIXIAODUI)
        {
            fan += 2;// 七小对
            fan_type |= GameConstant.MAHJONG_HU_CODE_QIXIAODUI;

            desc +="  七小对×2";
        } else if(pao_pl==null && pl.isMenQing())
        {
            fan +=1;
            fan_type |= GameConstant.MAHJONG_HU_CODE_MEN_QING;
            desc +=" 门清×1";
        }

        //清一色
        if( this.checkSameColor(pl))
        {
            //清一色跟碰碰胡分开判断 lyh
            fan +=1;
            fan_type |= GameConstant.MAHJONG_HU_CODE_QINGYISE;
            desc +="  清一色×1";
        }
        pl.setHuDesc(desc);
        //
        MahjongCheckResult result=new MahjongCheckResult();
        result.fanNum=fan;
        result.fanResult=fan_type;

        return result;
    }

    public boolean checkSameColor(Player pl) {
        if(pl==null)
            return false;
        //
        List<Byte> c1 = pl.getCardsInHand();
        List<CardDown> c2 = pl.getCardsDown();

        if (isOneColor(c1, c2))// 清一色
            return true;

        return false;

    }

    public boolean isOneColor(List<Byte> cardsInHand, List<CardDown> cardsDown) {
        int color_num = 0;
        boolean found0 = false;
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        //
        for (int i = 0; i < cardsInHand.size(); i++) {
            int bb = cardsInHand.get(i) & 0xff;


            int color = bb & GameConstant.MAHJONG_CODE_COLOR_MASK;
            if (color == 0 && found0 == false) {
                found0 = true;
                color_num++;
            } else if (color == 0x10 && found1 == false) {
                found1 = true;
                color_num++;
            } else if (color == 0x20 && found2 == false) {
                found2 = true;
                color_num++;
            }
            else if (color == 0x30 && found3 == false) {
                found3 = true;
                color_num++;
            }
            //
            if (color_num >= 2)
                return false;
        }
        //
        for (int i = 0; i < cardsDown.size(); i++) {
            int bb = cardsDown.get(i).cardValue & 0xff;

            int color = bb & GameConstant.MAHJONG_CODE_COLOR_MASK;
            //
            if (color == 0x0 && found0 == false) {
                found0 = true;
                color_num++;
            } else if (color == 0x10 && found1 == false) {
                found1 = true;
                color_num++;
            } else if (color == 0x20 && found2 == false) {
                found2 = true;
                color_num++;
            }else if (color == 0x30 && found3 == false) {
                found3 = true;
                color_num++;
            }
            //
            if (color_num >= 2)
                return false;
        }

        return true;
    }

    // 听，是否有听提示广播给其他玩家，是否可以换听张？
    private MahjongCheckResult do_check_ting(byte card, Player pl,GameTable gt) {

        List<Byte> c1 = pl.getCardsInHand();

        MahjongCheckResult res = new MahjongCheckResult();
        res.opertaion = GameConstant.MAHJONG_OPERTAION_TING;
        res.playerTableIndex = pl.getTablePos();


        try{
            // 把临时牌放进来
            if (card != 0) {
                pl.addCardInHand(card);
            }
            //
            int num = pl.getCardNumInHand();
            //
            for (int i = 0; i < num; i++)
            {
                Byte remove_card = 0;
                List<Byte> ting_list = new ArrayList<Byte>();
                // 这样的牌，估计是中断又回来的，只检测一次
                if (num == 1 || num == 4 || num == 7 || num == 10 || num == 13) {
                    i = num - 1;//
                    //
                    remove_card = 0;
                    for (int j = 0; j < num; j++) {
                        byte bb = c1.get(j);
                        ting_list.add(bb);
                    }
                }
                else
                {
                    for (int j = 0; j < num; j++) {
                        byte bb = c1.get(j);
                        if (i == j)// 每次移掉一张牌，再检测能否听牌，因为玩家现在刚抓了一张牌
                        {
                            remove_card = bb;
                            continue;
                        }
                        //
                        ting_list.add(bb);
                    }
                }
                //
                CardPatternCheckResult wan = new CardPatternCheckResult();
                CardPatternCheckResult tiao = new CardPatternCheckResult();
                CardPatternCheckResult tong = new CardPatternCheckResult();

                CardPatternCheckResult zi = new CardPatternCheckResult();

                //
                List<CardPatternCheckResult> tiao_results = new ArrayList<CardPatternCheckResult>();
                List<CardPatternCheckResult> tong_results = new ArrayList<CardPatternCheckResult>();
                List<CardPatternCheckResult> wan_results = new ArrayList<CardPatternCheckResult>();

                List<CardPatternCheckResult> zi_results = new ArrayList<CardPatternCheckResult>();

                wan.setUncheckedCards(getCardsByColor(ting_list,GameConstant.MAHJONG_CODE_COLOR_WAN));
                tiao.setUncheckedCards(getCardsByColor(ting_list,GameConstant.MAHJONG_CODE_COLOR_TIAO));
                tong.setUncheckedCards(getCardsByColor(ting_list,GameConstant.MAHJONG_CODE_COLOR_TONG));


                zi.setUncheckedCards(getCardsByColor(ting_list,GameConstant.MAHJONG_CODE_COLOR_ZI));
                //
                ting_check(wan_results, wan);
                if (wan_results.size() == 0) {
                    // System.out.println("当前万子不成牌");
                    continue;// 当前万子不成牌
                }
                //
                ting_check(tiao_results, tiao);
                if (tiao_results.size() == 0) {
                    // System.out.println("当前条子不成牌");
                    continue;// 当前条子听不了
                }

                //
                ting_check(tong_results, tong);
                if (tong_results.size() == 0) {
                    // System.out.println("当前筒子不成牌");
                    continue;// 当前筒子听不了
                }

                ting_check(zi_results, zi);
                if (zi_results.size() == 0) {
                    // System.out.println("当前子不成牌");
                    continue;// 当前子听不了
                }

                // 如果能听牌，把结果记录下来
                int t_res=ting_check_final(res,remove_card,pl,tiao_results, wan_results, tong_results, zi_results,gt );
                if(t_res>0) //可以听
                {
                    res.tingList.add(remove_card);
                }
                //
                ting_list.clear();
                ting_list = null;
                //
                tiao_results.clear();
                tong_results.clear();
                wan_results.clear();
                zi_results.clear();
            }


            //
            if (res.tingList.size() > 0)
                return res;// 听了

        }catch(Exception e)
        {
            System.out.println("check ting error=" + e);
        }finally{
            // 临时牌，移掉
            pl.removeCardInHand(card);
        }


        //

        return null;
    }

    public List<Byte> getCardsByColorWithoutHun(List<Byte> tl, int cc,byte hun1,byte hun2) {
        List<Byte> cs = new ArrayList<Byte>();
        for (int i = 0; i < tl.size(); i++)
        {
            byte b = tl.get(i);
            if(b==hun1 || b==hun2)
                continue;

            int color = b & GameConstant.MAHJONG_CODE_COLOR_MASK;
            if (color == cc) {
                cs.add(b);
            }
        }

        Collections.sort(cs);
        //
        return cs;
    }

    public List<Byte> getCardsByColorWithoutHun(List<Byte> tl, byte hun) {
        List<Byte> cs = new ArrayList<Byte>();
        for (int i = 0; i < tl.size(); i++)
        {
            byte b = tl.get(i);
            if(b==hun)
                continue;

            cs.add(b);

        }
        //
        return cs;
    }

    private void set_ting_type(CardPatternCheckResult ck, int hongzhongNum) {
        if (ck.getUncheckedCards().size() > 0)
            return;
        //
        if (ck.getDuiZiValue() == 0 && ck.danDiao == 0 && ck.shunZi == 0)// 如果之前没有听牌，说明已经成牌
        {
            ck.result = GameConstant.MAHJONG_TING_CODE_CHENG_PAI;
        } else if (ck.danDiao > 0 && hongzhongNum == 0) {
            ck.result = GameConstant.MAHJONG_TING_CODE_DAN_DIAO;
        } else if (ck.shunZi > 0 && (hongzhongNum == 0 || hongzhongNum == 2)) {
            ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI;
        }
        //
        else if (ck.getDuiziNum() == 1
                && (hongzhongNum == 0 || hongzhongNum == 2)) {
            ck.result = GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI;
        } else if (ck.getDuiziNum() == 2 && hongzhongNum == 0) {
            ck.result = GameConstant.MAHJONG_TING_CODE_TWO_DUI_ZI;
        }
    }

    private boolean could_ting_check(CardPatternCheckResult ck, int hongzhongNum) {
        int num_left = ck.getUncheckedCards().size();
        if (ck.danDiao > 0 && num_left > 0) {
            return false;
        }
        //
        if (hongzhongNum == 1 && (num_left == 1 || num_left == 2)) {
            return false;// 有一张红中，只可能听红中
        }

        if (hongzhongNum > 0 && num_left == 1) {
            return false;// 有红中，还剩一张，肯定听不了
        }

        //单调同时听顺子的情况不能成牌
        if(ck.danDiao>0 && ck.isTingCardShun){
            return false;
        }

        if(ck.danDiao>0 && ck.getDuiZiValue()>0){
            return false;
        }

        // 如果只有一张牌
        if (num_left == 0) {
            set_ting_type(ck, hongzhongNum);
            return true;
        } else if (num_left == 1) {
            if (ck.getDuiZiValue() == 0 && ck.danDiao == 0 && ck.shunZi == 0) {
                // 没有听其他牌，那就单调这张牌，可能听
                ck.danDiao = ck.getUncheckedCards().remove(0);
                ck.result = GameConstant.MAHJONG_TING_CODE_DAN_DIAO;
                return true;
            } else {
                // 多张单牌，听不了
                return false;
            }
        } else if (num_left == 2)// 如果只有两张牌
        {
            byte c1 = ck.getUncheckedCards().get(0);
            byte c2 = ck.getUncheckedCards().get(1);
            if (c1 == c2) {
                if ((ck.getDuiziNum() == 0 && (hongzhongNum == 0 || hongzhongNum == 2))// 是否可以再添加一个对子
                        || (hongzhongNum == 0 && ck.getDuiziNum() == 1
                        && ck.danDiao == 0 && ck.shunZi == 0)) {
                    ck.getUncheckedCards().remove(0);
                    ck.getUncheckedCards().remove(0);
                    //
                    ck.addDuizi(c1);
                    //
                    set_ting_type(ck, hongzhongNum);
                    //
                    return true;
                } else {
                    return false;// 听不了，3个对子
                }
            } else if ((hongzhongNum == 0 || hongzhongNum == 2)
                    && ck.getDuiziNum() < 2 && ck.danDiao == 0
                    && ck.shunZi == 0)// 没有单调，没有听顺子，就可能听这2牌的,
            {
                if (c1 == c2 - 1 || c1 == c2 + 1) {
                    ck.shunZi = c1 | (c2 << 8);
                    ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI;

                    return true;
                }
                if (c1 == c2 - 2 || c1 == c2 + 2) {
                    ck.shunZi = c1 | (c2 << 8);
                    ck.result = GameConstant.MAHJONG_TING_CODE_SHUN_ZI_KA_ZHANG;
                    return true;
                }
                //
                return false;// 2张不连续牌，听不了,或者有单调，又有2张不连续的
            }
            return false;// 至少2张单牌
        }
        //
        return false;// 不会到这里

    }

    // 这一组牌能听，就直接返回，至于听什么牌，能听多种组合，客户端暂时不需要展示，就不判断了
    public void ting_check(List<CardPatternCheckResult> outs, CardPatternCheckResult ck, int hongzhongNum) {
        if (ck.getUncheckedCards().size() <= 2) {
            if (could_ting_check(ck, hongzhongNum)) {
                outs.add(ck);
            }
            return;
        }
        //

        //
        byte b1 = ck.getUncheckedCards().get(0);
        byte b2 = ck.getUncheckedCards().get(1);
        byte b3 = ck.getUncheckedCards().get(2);
        //

        // 第一张牌单调,听牌
        if (ck.danDiao == 0 && hongzhongNum == 0) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);

            byte danDiao = ckx.getUncheckedCards().remove(0);
            ckx.danDiao = danDiao;
            //
            ting_check(outs, ckx, hongzhongNum);
        }
        // 第一张作为对子
        if (ck.danDiao == 0 && (b1 == b2)
                && (ck.couldAddMoreDuizi(hongzhongNum))) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            ckx.addDuizi(b1);
            //
            ting_check(outs, ckx, hongzhongNum);

        }
        // 第一张作为刻牌aaa,成牌
        if (b1 == b2 && b2 == b3) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            ckx.setKeziNum(ckx.getKeziNum()+1);

            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            ting_check(outs, ckx, hongzhongNum);

        }
        // 第一张作为顺牌abc，成牌
        if (ck.foundCard((byte) (b1 + 1)) && ck.foundCard((byte) (b1 + 2))) {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            ckx.shunZiNum++;// 加多一个顺子

            ckx.getUncheckedCards().remove(0);
            ckx.remove((byte) (b1 + 1));
            ckx.remove((byte) (b1 + 2));
            //
            int cc = b1 | ((b1 + 1) << 8) | ((b1 + 2) << 16);
            ckx.getCheckedCards().add(cc);
            //
            ting_check(outs, ckx, hongzhongNum);

        }

        //
        if (hongzhongNum == 0 || hongzhongNum == 2)// 只有红中等于0的时候，才能听其他牌，否则只能听好红中
        {
            // 就听第一张和某一张组成顺子，听顺子
            if (((ck.getDuiziNum() != 2) && !(ck.getDuiziNum() == 1 && hongzhongNum == 1))) {
                if (ck.danDiao == 0 && ck.shunZi == 0
                        && ck.foundCard((byte) (b1 + 1))) {
                    CardPatternCheckResult ckx = new CardPatternCheckResult();
                    ckx.copy(ck);
                    //
                    ckx.getUncheckedCards().remove(0);
                    ckx.remove((byte) (b1 + 1));
                    //
                    ckx.shunZi = b1 | ((b1 + 1) << 8);// 如果后面的面，检测都成牌，这就可能听顺子
                    //
                    ckx.isTingCardShun = true; // 标示了这个就不能再对对胡
                    ting_check(outs, ckx, hongzhongNum);
                }

            }

            // 就听第一张和某一张组成顺子，听卡顺子
            if (((ck.getDuiziNum() != 2) && !(ck.getDuiziNum() == 1 && hongzhongNum == 1))) {
                if (ck.danDiao == 0 && ck.shunZi == 0
                        && ck.foundCard((byte) (b1 + 2))) {
                    CardPatternCheckResult ckx = new CardPatternCheckResult();
                    ckx.copy(ck);
                    //
                    ckx.getUncheckedCards().remove(0);
                    ckx.remove((byte) (b1 + 2));
                    //
                    ckx.shunZi = b1 | ((b1 + 2) << 8);// 如果后面的面，检测都成牌，这就可能听卡顺子

                    ckx.isTingCardShun = true; // 标示了这个就不能再对对胡
                    //
                    ting_check(outs, ckx, hongzhongNum);
                }
            }
        }

    }

    public List<Byte> getCardsByColor(List<Byte> tl, int cc) {
        List<Byte> cs = new ArrayList<Byte>();
        for (int i = 0; i < tl.size(); i++) {
            byte b = tl.get(i);


            int color = b & GameConstant.MAHJONG_CODE_COLOR_MASK;
            if (color == cc) {
                cs.add(b);
            }
        }
        //
        return cs;
    }

    //只检测跟这个新摸起来的card相关的杠
    @Override
    public MahjongCheckResult checkGang(GameTable gt,byte card, Player pl,boolean isMyCard) {
        if (gt.getCardLeftNum() <= 0)
            return null;

        if (pl == null)
            return null;

        if (pl.isWin())
            return null;

        return check_gang_without_hun(gt, card, pl, isMyCard);
    }

    private CardPatternCheckResult x_hu_check(List<Byte>c1,int color,byte hun_cd,byte bao,int hun_num ) {
        // 0x11是赖子，查看条听牌时，特殊处理
        for(int i = 0; i <= hun_num; i++)
        {
            CardPatternCheckResult cc = new CardPatternCheckResult();
            cc.setUncheckedCards(getCardsByColorWithoutHun(c1,color,hun_cd,bao));

            if (hu_check(cc,i))
            {
                return cc;
            }
        }

        return null;
    }

    private boolean hu_check(CardPatternCheckResult ck,int hunNum){
        if (ck.getUncheckedCards().size() == 0)
        {
            // TODO
            if (ck.getDuiZiValue() == 0 && ck.danDiao == 0 && ck.shunZi == 0)// 如果之前没有听牌，说明已经成牌
            {
                ck.result = GameConstant.MAHJONG_TING_CODE_CHENG_PAI;
                return true;
            } else if (ck.getDuiziNum() == 1) {
                ck.result = GameConstant.MAHJONG_TING_CODE_ONE_DUI_ZI;
                return true;
            }

            return false;
        }
        //
        byte b1 = ck.getUncheckedCards().get(0);
        //剩一张，用2张混子，组成一个刻子
        if (ck.getUncheckedCards().size() == 1)
        {

            //拿混子当对子
            if (hunNum>0 && ck.getDuiziNum() == 0)
            {
                CardPatternCheckResult ckx = new CardPatternCheckResult();
                ckx.copy(ck);
                //
                ckx.getUncheckedCards().remove(0);
                //
                ckx.addDuizi(b1);


                //
                ckx.hunUsed +=1;
                if (hu_check(ckx,hunNum-1)) {
                    ck.result = ckx.result;
                    ck.copy(ckx);
                    return true;
                }

            }

            if(hunNum>=2)
            {
                CardPatternCheckResult ckx = new CardPatternCheckResult();
                ckx.copy(ck);
                // 加多一个刻子
                ckx.setKeziNum(ckx.getKeziNum() + 1);
                //
                ckx.getUncheckedCards().remove(0);

                //
                int cc = b1 | (b1 << 8) | (b1 << 16);


                ckx.getCheckedCards().add(cc);
                //
                ckx.hunUsed +=2;
                if (hu_check(ckx,hunNum-2))
                {
                    ck.result = ckx.result;
                    ck.copy(ckx);
                    return true;
                }
            }
            return false;
        }

        byte b2 = ck.getUncheckedCards().get(1);

        //字牌不能做顺子
        boolean is_zi=false;
        int color1=b1&GameConstant.MAHJONG_CODE_COLOR_MASK;
        if(color1==GameConstant.MAHJONG_CODE_COLOR_ZI)
            is_zi=true;

        boolean is_tiao = false;
        int color2=b1&GameConstant.MAHJONG_CODE_COLOR_MASK;
        if(color2==GameConstant.MAHJONG_CODE_COLOR_TIAO)
            is_tiao=true;

        //

        // 第一张作为对子,只能有一个对子
        if (b1 == b2 && ck.getDuiziNum() == 0)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            ckx.addDuizi(b1);
            //
            if (hu_check(ckx,hunNum)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }
        //拿混子当对子,手里只有2张牌的情况
        if (hunNum>0 && ck.getDuiziNum() == 0 && ck.getUncheckedCards().size()==2)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //
            ckx.getUncheckedCards().remove(0);
            //
            ckx.addDuizi(b1);

            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }
        }

        if(is_tiao && hunNum >0){
            if(b1 == 0x12 && b2 == 0x13){
                CardPatternCheckResult ckx = new CardPatternCheckResult();
                ckx.copy(ck);
                ckx.shunZiNum++;
                ckx.getUncheckedCards().remove(0);
                ckx.getUncheckedCards().remove(0);

                //
                int cc =0;
                cc=0x11 | ((0x12) << 8) | ((0x13) << 16);

                ckx.getCheckedCards().add(cc);

                ckx.hunisxiaoji = true;
                //
                ckx.hunUsed +=1;
                if (hu_check(ckx,hunNum-1)) {
                    ck.result = ckx.result;
                    ck.copy(ckx);
                    return true;
                }
            }

        }

        //
        if (ck.getUncheckedCards().size()< 3 )
        {
            if(hunNum==0)
                return false;
            //
            if(b1==b2-1 || b1==b2-2)
            {
                if(!is_zi){
                    CardPatternCheckResult ckx = new CardPatternCheckResult();
                    ckx.copy(ck);
                    //

                    //
                    ckx.shunZiNum++;
                    ckx.getUncheckedCards().remove(0);
                    ckx.getUncheckedCards().remove(0);

                    //
                    int cc =0;
                    if(b1==b2-2){
                        cc=b1 | ((b1+1) << 8) | ((b2) << 16);
                    }
                    else if(b1+2<=9){
                        cc=b1 | ((b2) << 8) | ((b1+2) << 16);
                    }
                    else{
                        cc=(b1-1) | ((b1) << 8) | ((b2) << 16);

                        //(b1-1) 是小鸡
                        if(b1 == 0x12){
                            ckx.hunisxiaoji = true;
                        }
                    }
                    //
                    ckx.getCheckedCards().add(cc);
                    //
                    ckx.hunUsed +=1;
                    if (hu_check(ckx,hunNum-1)) {
                        ck.result = ckx.result;
                        ck.copy(ckx);
                        return true;
                    }
                }

            }

            // 第一张作为刻牌aaa,成牌
            if (b1 == b2 )
            {
                CardPatternCheckResult ckx = new CardPatternCheckResult();
                ckx.copy(ck);
                // 加多一个刻子
                ckx.setKeziNum(ckx.getKeziNum()+1);
                //

                ckx.getUncheckedCards().remove(0);
                ckx.getUncheckedCards().remove(0);
                //
                int cc = b1 | (b1 << 8) | (b1 << 16);
                ckx.getCheckedCards().add(cc);


                ckx.hunUsed +=1;
                //
                if (hu_check(ckx,hunNum))
                {
                    ck.result = ckx.result;
                    ck.copy(ckx);
                    return true;
                }

            }

            return false;
        }

        byte b3 = ck.getUncheckedCards().get(2);
        //
        // 第一张作为刻牌aaa,成牌
        if (b1 == b2 && b2 == b3)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            // 加多一个刻子
            ckx.setKeziNum(ckx.getKeziNum()+1);
            //

            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            if (hu_check(ckx,hunNum)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }

        // 第一张作为顺牌abc，成牌
        if ( is_zi==false && ck.foundCard((byte) (b1 + 1)) && ck.foundCard((byte) (b1 + 2)))
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //

            //
            ckx.shunZiNum++;
            ckx.getUncheckedCards().remove(0);
            ckx.remove((byte) (b1 + 1));
            ckx.remove((byte) (b1 + 2));
            //
            int cc = b1 | ((b1 + 1) << 8) | ((b1 + 2) << 16);
            ckx.getCheckedCards().add(cc);
            //
            if (hu_check(ckx,hunNum)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }

        //////////////////////////////////////////////////////////////////////////////////
        //拿混子当对子
        if (hunNum>0 && ck.getDuiziNum() == 0)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //
            ckx.getUncheckedCards().remove(0);
            //
            ckx.addDuizi(b1);


            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }

        //用一张混子做刻子
        if (hunNum>0 && b1 == b2)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            // 加多一个刻子
            ckx.setKeziNum(ckx.getKeziNum()+1);
            //
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(0);
            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }


        //用一张混子做刻子
        if (hunNum>0 && b1 == b3 && b2!=b3)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            // 加多一个刻子
            ckx.setKeziNum(ckx.getKeziNum()+1);
            //
            ckx.getUncheckedCards().remove(0);
            ckx.getUncheckedCards().remove(1);
            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }

        // 第一张作为顺牌abc，成牌
        if (hunNum>0 && is_zi==false && ck.foundCard((byte) (b1 + 2)) )
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //

            //
            ckx.shunZiNum++;
            ckx.getUncheckedCards().remove(0);
            ckx.remove((byte) (b1 + 2));

            //
            int cc = b1 | ((b1 + 1) << 8) | ((b1 + 2) << 16);
            ckx.getCheckedCards().add(cc);
            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }


        // 第一张作为顺牌abc，成牌
        if (hunNum>0 && is_zi==false && ck.foundCard((byte) (b1 + 1)) )
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            //

            //
            ckx.shunZiNum++;
            ckx.getUncheckedCards().remove(0);
            ckx.remove((byte) (b1 + 1));

            //
            int cc = b1 | ((b1 + 1) << 8) | ((b1 + 2) << 16);
            ckx.getCheckedCards().add(cc);
            //
            ckx.hunUsed +=1;
            if (hu_check(ckx,hunNum-1)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }

        //用2张混子做刻子
        if (hunNum>=2)
        {
            CardPatternCheckResult ckx = new CardPatternCheckResult();
            ckx.copy(ck);
            // 加多一个刻子
            ckx.setKeziNum(ckx.getKeziNum()+1);
            //
            ckx.getUncheckedCards().remove(0);

            //
            int cc = b1 | (b1 << 8) | (b1 << 16);
            ckx.getCheckedCards().add(cc);
            //
            ckx.hunUsed +=2;
            if (hu_check(ckx,hunNum-2)) {
                ck.result = ckx.result;
                ck.copy(ckx);
                return true;
            }

        }
        //


        return false;
    }

    private int hasXCardNum(List<Byte> cards, byte card) {
        int num = 0;
        for (int i = 0; i < cards.size(); i++) {
            byte c1 = cards.get(i);
            if (c1 == card) {
                num ++;
            }
        }

        return num;
    }

    private void printCards(List<Byte> list) {
        String cardStr = "";
        for(byte card : list) {
            cardStr += Integer.toHexString(card) + "_";
        }

        logger.info("cards=" + cardStr);
    }
}
