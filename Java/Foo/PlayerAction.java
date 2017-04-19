package com.linyun.xgame.admin.player;

import com.linyun.base.common.ExportExcel;
import com.linyun.base.common.QueryForPage;
import com.linyun.base.common.StringUtils;
import com.linyun.base.constant.GameConstant;
import com.linyun.base.constant.LogConstant;
import com.linyun.base.constant.RedisKeyConstant;
import com.linyun.base.domain.*;
import com.linyun.base.server.PlayerOnlineManager;
import com.linyun.xgame.admin.constant.AdminSystemConstant;
import com.linyun.xgame.cache.JedisService;
import com.linyun.xgame.common.BaseAdminContext;
import com.linyun.xgame.common.DateService;
import com.linyun.xgame.common.MD5Service;
import com.linyun.xgame.common.SpringService;
import com.linyun.xgame.constant.GameConfigConstant;
import com.linyun.xgame.dao.IQunzhuPlayerDAO;
import com.linyun.xgame.domain.GameTable;
import com.linyun.xgame.service.IDBServerPlayerService;
import com.linyun.xgame.service.IPayService;
import com.linyun.xgame.service.IPlayerService;
import com.linyun.xgame.service.ISystemConfigService;
import com.linyun.xgame.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 玩家信息 如果struts 使用通配符*等xwork2的特性的话，必须继承BasalAction 类（继承xwork2 的框架ActionSupport
 * 类），不能用继承BaseAction
 */
public class PlayerAction extends QueryForPage {

	private List<Player> playerList;

	private Player playerBean;
	
	private int index;

	private String id;

	private String account;
	private String qunZhuID="";
	
	private int queryType = 0;
	
	private int order = 0;
	
	private String startTime = null;
	
	private String endTime = null;
	
	private int receiveIndex = 0;
	
	private int gold = 0;
	
	private int queryPlayerType = 0;
	
	private String errMsg="无此账号信息";
	private String parentName = "";
	private static Logger logger = Logger.getLogger(PlayerAction.class);
	private QunzhuPlayer qunzhuPlayerBean = new QunzhuPlayer();
	
	private String qunzhuPhoneNumber="";//手机号
	private String qunzhuWxName ="";//微信昵称
	private String qunzhuWxID="";//微信id
	private String qunzhuTrueName ="";//真实姓名	
	private String qunzhuRemark="";

	public String browsePlayer() {
		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		
		if(this.pageSize == 0)
		{
			this.setPageSize(20);
		}	
		if(qunZhuID!=null && qunZhuID.length()>2)
		{
			playerList = dbplayerService.getSubPlayerListByUpPlayerID(this.qunZhuID);
			total = playerList.size();
			if(total>pageSize)
				pageSize=total;
		}
		else if(account!=null && !account.equals(""))
		{
			/*
			total = playerService.getCustomTotalCount(account);
			doPage(total, this.getPageSize());
			playerList = playerService.getAllCustomPlayerByPage(this.startRownum, this.endRownum,account);
			*/
			if(queryType == 0)
			{
				//ID查询
				total = playerService.getPlayersByPlayerIDCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerIDByPage(this.startRownum, this.endRownum, account, startTime, endTime, order);

			}
			else if (queryType == 1)
			{
				//帐号查询
				total = playerService.getPlayersByPlayerAccountCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerAccountByPage(this.startRownum, this.endRownum,account, startTime, endTime, order);

			}
			else if (queryType == 2)
			{
				//昵称查询
				total = playerService.getPlayersByPlayerNameCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerNameByPage(this.startRownum, this.endRownum,account, startTime, endTime, order);
			}
		}
		else
		{
			total = playerService.getTotalCount(startTime, endTime, queryPlayerType);
			doPage(total, this.getPageSize());
			playerList = playerService.getAllPlayerByPage(this.startRownum, this.endRownum , startTime, endTime, order, queryPlayerType);

			
			if(queryPlayerType>=3 && dbplayerService!=null)
			{
				
				for(int i=0;i<playerList.size();i++)
				{
					Player plx=playerList.get(i);
					dbplayerService.caculate_m_pay_back(plx);
				}
			}
		}
		return SUCCESS;
	}

	public String kefuPlayer(){
		
		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		if(this.pageSize == 0)
		{
			this.setPageSize(20);
		}	
		if(qunZhuID!=null && qunZhuID.length()>2)
		{
			playerList = dbplayerService.getSubPlayerListByUpPlayerID(qunZhuID);
			total = playerList.size();
			if(total>pageSize)
				pageSize=total;
		}
		if(account!=null && !account.equals(""))
		{
			if(queryType == 0)
			{
				//ID查询
				total = playerService.getPlayersByPlayerIDCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerIDByPage(this.startRownum, this.endRownum, account, startTime, endTime, order);

			}
			else if (queryType == 1)
			{
				//帐号查询
				total = playerService.getPlayersByPlayerAccountCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerAccountByPage(this.startRownum, this.endRownum,account, startTime, endTime, order);

			}
			else if (queryType == 2)
			{
				//昵称查询
				total = playerService.getPlayersByPlayerNameCount(account, startTime, endTime);
				doPage(total, this.getPageSize());
				playerList = playerService.getPlayersByPlayerNameByPage(this.startRownum, this.endRownum,account, startTime, endTime, order);
			}
			if(playerList.size()>0){
				
				playerBean = playerList.get(0);
				this.setPlayerInfoOtherData();
				return "kefuDetial";
			}
		}
		
		return SUCCESS;
	}
	public void setPlayerInfoOtherData()
	{
		if(playerBean == null ){
			return;
		}
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		if(playerBean.getParentIndex()!=0){
			Player parentP = dbplayerService.getPlayerByPlayerIndex(playerBean.getParentIndex());
			this.setParentName(parentP.getPlayerName());
		}
		IQunzhuPlayerDAO qpd =dbplayerService.getQunzhuPlayerDAO();
		qunzhuPlayerBean = qpd.getQunzhuByIndex(playerBean.getPlayerIndex()); 
		if(qunzhuPlayerBean==null){
			qunzhuPlayerBean=new QunzhuPlayer();
		}
		
	}
	
	private int payState = -1;
	private Integer onePageNum;
	private int pages;
	private Integer  currPageNO;
	private List<PayHistory> payHistoryList;

	public String exportExcel()
	{
		ExportExcel excel=new ExportExcel();  
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		String playerIndex = baseAdminContext.getOssUser().getUsername();
		
		String time = request.getParameter("exportTime");
		Player pl = playerService.getPlayerByPlayerIndex(Integer.parseInt(playerIndex));
		
		HashMap<String, Object> maps = new HashMap<String, Object>();
		maps.put("playerID", pl.getPlayerID());
		maps.put("operationType", LogConstant.OPERATION_TYPE_SUB_DIAMOND);
		maps.put("beginTime", time);
		maps.put("endTime", time);
		maps.put("startRownum", 0);
		maps.put("endRownum", 10000);
		List<PlayerOperationLog> logs = dbplayerService.getPlayerOperationLog(maps);
		List<String[]>content = new ArrayList<String[]>();
		String[] title={"日期","充值时间","充值客服id","玩家id","购买金额","购买房卡数","增加房卡数","额外房卡数（增加房卡数-购买房卡数）","备注"};
		for(int i=0;i<logs.size();i++)
		{
			PlayerOperationLog log = logs.get(i);
			String detail = log.getOpDetail();
			String id = "";
			try{
			id=detail.split("]")[0].split("=")[1];
			}catch(Exception e){
				e.printStackTrace();
				id="";
			}
			
			String[] temp={StringUtil.date2String(log.getCreateTime()),StringUtil.date2String(log.getCreateTime()),pl.getPlayerIndex().toString(),id,String.valueOf(log.getPlayerBuyMoney()),String.valueOf(log.getPlayerBuyGold()),
					String.valueOf(log.getOpGold()),String.valueOf(log.getOpGold()-log.getPlayerBuyGold()),log.getRemark()};
			content.add(temp);
		}  
	    excel.exportExcel("recharge_"+pl.getPlayerIndex()+"_"+StringUtil.timestamp2String(DateService.getCurrentUtilDate().getTime())+".xls",title, content);   
	    return "exchangeSuccess"; 
	}
	
	public String playerPay(){
		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		// 开始时间和结束时间
		Date beginTime = null, endTime = null;
		endTime = DateService.getCurrentDayLastUtilDate();
		
		// 玩家
		String playerID = null;
		Player player = null;
		
		if(StringUtils.isInteger(account) && Integer.parseInt(account)>0)
		{
		    index = Integer.parseInt(account);
			player = playerService.getPlayerByPlayerIndex(index);			
			if (player != null) {
				playerID = player.getPlayerID();
			}
		} 
		

		IPayService payService = (IPayService) SpringService.getBean("payService");
		// 总记录数
		total = payService.getOnlyPayMoneyByTime(playerID, -1, null, beginTime, endTime, payState);

		if (onePageNum == null || onePageNum == 0) {
			onePageNum = 20; // 每页20条记录
		}
		
		pages = total % onePageNum > 0 ? (total / onePageNum + 1) : (total / onePageNum);
		// 当前页设置
		if (currPageNO == null || currPageNO.intValue() == 0) {
			currPageNO = 1;
		} else if (currPageNO.intValue() > pages) {
			currPageNO = pages;
		}
		Integer beginNum = (currPageNO - 1) * onePageNum;
		if (beginNum < 0)
			beginNum = 0;
		// 充值集合
		payHistoryList = payService.getPayHistoryListByTime(playerID, -1, null, beginTime, endTime, beginNum, onePageNum, payState);
		
		
		return "playerPay";
	}
	
	public String queryPlayerByAccount() {

		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		
		if(account!=null && !account.equals(""))
		{
			if(queryType == 0)
			{
				//ID查询
				if((account.length() < 10) && StringUtils.isInteger(account) && Integer.parseInt(account)>0)
				{
					playerBean = playerService.getPlayerByPlayerIndex(Integer.parseInt(account));
				}
			}
			else if (queryType == 1)
			{
				//帐号查询
				playerBean = playerService.getPlayerByAccount(account);
			}
			else if (queryType == 2)
			{
				//昵称查询
				playerBean = playerService.getPlayerByPlayerName(account);
			}	
		}
		
		if (playerBean == null) 
		{
			return "failed";
		}

		return "detial";	
	}
	
	public int getOnePageNum() {
		return onePageNum;
	}

	public void setOnePageNum(int onePageNum) {
		this.onePageNum = onePageNum;
	}

	public String queryPlayerByIndex() {

		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");

		playerBean = playerService.getPlayerByPlayerIndex(index);
		if (playerBean == null) {
			return "failed";
		}
		return "detial";

	}
	public String kefuAlterPlayerPassword()
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		if(this.checkKefu(baseAdminContext.getOssUser().getUsername()) == false){
			this.errMsg="没有操作权限，请联系管理员";
			return "kefuFailed";
		}
		String ans= this.alterPlayerPassword();
		if(ans.equals("detial")){
			return "kefuDetial";
		}
		return ans;
	}
	public String alterPlayerPassword()
	{
		String playerID =  ServletActionContext.getRequest().getParameter("playerID");
		String pwd =  ServletActionContext.getRequest().getParameter("pwd");
		
		if(pwd.length()<3||pwd.length()>12)
			return "ajax";
		
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		Player pl = dbplayerService.getPlayerByPlayerID(playerID);
		
		if(pl!=null){
			dbplayerService.updatePlayerPassword(pl, pl.getPassword(),MD5Service.encryptString(pwd));
		}
		
		try {
			ServletActionContext.getResponse().getWriter().write("success");
			ServletActionContext.getResponse().getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "ajax";
	}
	
	public String onLineState()
	{
		String playerID =  ServletActionContext.getRequest().getParameter("playerID");
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		Player pl = dbplayerService.getPlayerByPlayerIDFromCache(playerID);
		
		
		int isOnline = 0;
		if(pl!=null&&pl.isOnline()){
			isOnline =1;
		}
		
		try {
			ServletActionContext.getResponse().getWriter().write(isOnline+"");
			ServletActionContext.getResponse().getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "ajax";
	}


	public String allOnlineNum()
	{
		int num = PlayerOnlineManager.getOnlines();
		try {
			ServletActionContext.getResponse().getWriter().write(num+"");
			ServletActionContext.getResponse().getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "ajax";
	}

	public String detialPlayer() {
		IPlayerService playerService = (IPlayerService) SpringService.getBean("playerBaseService");
		playerBean = playerService.getPlayerByID(id);
		this.setPlayerInfoOtherData();
		return "detial";
	}
	public String kefuDetialPlayer() {
		return this.dealKefuReturn(this.detialPlayer());
	}
	public String dealKefuReturn(String ret)
	{
		if(ret.equals("detial")){
			return "kefuDetial";
		}
		if(ret.equals("failed"))
		return "kefuFailed";
		return ret;
	}
	
	public String updatePlayerGold() {		
		
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		
		
		if(method.equals("POST"))
		{
			String playerID = request.getParameter("playerID");
			String gold = request.getParameter("playerGold");
		
			if(!StringUtils.isInteger(gold) || Integer.parseInt(gold)<0){
				actionMsg = "金币必须为1-11正整数字符!";
				return queryPlayerByAccount();
			}			
			
			//更新缓存
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");	
			
			Player pl = dbplayerService.getPlayerByPlayerID(playerID);
			
			int plGold = pl.getGold();
			
			if (null != dbplayerService)
			{
				//Player pl = dbplayerService.getPlayerByPlayerIDFromCache(playerID);			
				if(pl!=null)
				{
					pl.setGold(Integer.parseInt(gold));
					
					//修改记录写入日志
					String detail = "" + baseAdminContext.getOssUser().getUsername()+"手动重置玩家金币, 原始金币=" + plGold +"修改后=" + gold;
					dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), pl.getGold(), LogConstant.OPERATION_TYPE_ADD_GOLD, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, Integer.parseInt(gold), detail, LogConstant.MONEY_TYPE_GOLD);
				}
			}
			
			//更新数据库
			IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
			
			if (null != playerService)
			{
				playerService.updatePlayerGold(playerID, Integer.parseInt(gold));		
				
				//修改记录写入日志
				String detail = "" + baseAdminContext.getOssUser().getUsername()+"手动重置玩家金币, 原始金币=" + plGold +"修改后=" + gold;
				dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), Integer.parseInt(gold), LogConstant.OPERATION_TYPE_ADD_GOLD, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, Integer.parseInt(gold), detail, LogConstant.MONEY_TYPE_GOLD);
				
				playerBean = playerService.getPlayerByID(playerID);
				if (playerBean == null) {
					return "failed";
				}

				return "detial";
			}			
		}
		
		return "failed";
	}
	public String kefuUpdateQunzhuPlayer()
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		if(this.checkKefu(baseAdminContext.getOssUser().getUsername()) == false){
			this.errMsg="没有操作权限，请联系管理员";
			return "kefuFailed";
		}
		String ans= this.updateQunzhuPlayer();
		return this.dealKefuReturn(ans);
	}
	public String updateQunzhuPlayer()
	{
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		if(method.equals("POST"))
		{			
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
			if(dbplayerService == null){
				this.errMsg="系统繁忙";
				return "failed";
			}
			String playerID = request.getParameter("playerID");
			String playerWxName = request.getParameter("playerWxName");
			String playerTelNo = request.getParameter("playerTelNo");
			String playerTrueName = request.getParameter("playerTrueName");
			String playerWxId = request.getParameter("playerWxId");
			String qunzhuRemark = request.getParameter("qunzhuRemark");
			IQunzhuPlayerDAO qpd = dbplayerService.getQunzhuPlayerDAO();
						
			Player qunzhu = dbplayerService.getPlayerByPlayerID(playerID);
			if(qunzhu == null){
				this.errMsg="查询玩家失败";
				return "failed";
			}
			QunzhuPlayer qp = qpd.getQunzhuByIndex(qunzhu.getPlayerIndex());
			if(qp!=null){
				qp.setPhoneNumber(playerTelNo);
				qp.setWxID(playerWxId);
				qp.setWxName(playerWxName);
				qp.setPlayerType(qunzhu.getPlayerType());
				qp.setTrueName(playerTrueName);
				qp.setRemark(qunzhuRemark);
				qp.setPlayerID(qunzhu.getPlayerID());
				qp.setPlayerIndex(qunzhu.getPlayerIndex());
				qpd.update(qp);
			}else{
				qp = new QunzhuPlayer();
				qp.setPhoneNumber(playerTelNo);
				qp.setWxID(playerWxId);
				qp.setWxName(playerWxName);
				qp.setPlayerType(qunzhu.getPlayerType());
				qp.setTrueName(playerTrueName);
				qp.setRemark(qunzhuRemark);
				qp.setPlayerID(qunzhu.getPlayerID());
				qp.setPlayerIndex(qunzhu.getPlayerIndex());
				qpd.insert(qp);
			}
			logger.info(baseAdminContext.getOssUser().getUsername()+"后台操作,修改群主信息,tel:"+playerTelNo+",wxid:"+playerWxId+",wxname:"+playerWxName+",tname:"+playerTrueName+",remark:"+qunzhuRemark);
			playerBean = dbplayerService.getPlayerByID(playerID);
			this.setPlayerInfoOtherData();
			return "detial";
		}
		return "failed";
	}
	
	public String kefuUpdatePlayerDiamond()
	{
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		
		
		if(method.equals("POST"))
		{
			if(this.checkKefu(baseAdminContext.getOssUser().getUsername()) == false){
				this.errMsg="没有操作权限，请联系管理员";
				return "kefuFailed";
			}
			String playerID = request.getParameter("playerID");
			String sendPlayerDiamond = request.getParameter("sendPlayerDiamond");
			String buyMoney = request.getParameter("buyMoney");
			String buyGold = request.getParameter("buyGold");
			String addPlayerDiamondRemark = request.getParameter("addPlayerDiamondRemark");
			if(buyMoney.length() == 0 && buyGold.length() == 0 && sendPlayerDiamond.length()==0){
				this.errMsg="请填写房卡";
				String ans= queryPlayerByAccount();
				return this.dealKefuReturn(ans);
			}
			if(buyMoney.length() == 0 && buyGold.length() != 0){
				this.errMsg="请填写金额";
				String ans= queryPlayerByAccount();
				return this.dealKefuReturn(ans);
			}
			if(buyMoney.length() != 0 && buyGold.length() == 0){
				this.errMsg="请填写购买房卡数";
				String ans= queryPlayerByAccount();
				return this.dealKefuReturn(ans);
			}
			
			int buyGoldInt = 0,sendGoldInt = 0;
			if(buyGold.length()!=0){
				buyGoldInt = Integer.parseInt(buyGold);
			}
			if(sendPlayerDiamond.length()!=0){
				sendGoldInt = Integer.parseInt(sendPlayerDiamond);
			}
			int addPlayerDiamond = buyGoldInt+sendGoldInt;
			
			if(buyMoney.length() == 0){
				buyMoney = "0";
			}
			if(buyGold.length() == 0){
				buyGold = "0";
			}
			if(addPlayerDiamondRemark.length() == 0){
				addPlayerDiamondRemark = "";
			}
			
			//
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");								
			IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
			if (null != dbplayerService && playerService!=null)
			{	
				Player pl = dbplayerService.getPlayerByPlayerID(playerID);
				Player kefuPlayer = dbplayerService.getPlayerByPlayerIndex(Integer.parseInt(baseAdminContext.getOssUser().getUsername()));
				
				if(kefuPlayer.getDiamond()<(addPlayerDiamond)){
					this.errMsg="余额不足";
					return "kefuFailed";
				}
				if(pl.getPlayerIndex() == Integer.parseInt(baseAdminContext.getOssUser().getUsername())){
					this.errMsg="不能给自己充值";
					return "kefuFailed";
				}
				int playerDiamond = pl.getDiamond()+addPlayerDiamond;
				String remark ="客服["+kefuPlayer.getPlayerIndex()+"]充值,"+pl.getDiamond()+"->"+playerDiamond;
				
				//更新缓存
				dbplayerService.update_player_diamond(playerID,playerDiamond);
				//更新db
				playerService.updatePlayerDiamond(playerID, playerDiamond,pl.getVipExp());
				//记log
				dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), playerDiamond, LogConstant.OPERATION_TYPE_ADD_DIAMOND, 
						LogConstant.OPERATION_TYPE_KEFU_SEND_TO_OTHER_PLAYER_DIAMOND, (addPlayerDiamond), remark, LogConstant.MONEY_TYPE_DIAMOND,Integer.parseInt(buyMoney),Integer.parseInt(buyGold),
						addPlayerDiamondRemark);

				int kefuPlayerDiamond = kefuPlayer.getDiamond()-addPlayerDiamond;
				pl.setDiamond(playerDiamond);
				dbplayerService.addPlayerToUidMapCache(pl.getPlayerIndex(),GameConstant.GAME_ID, pl);

				//更新缓存 
				dbplayerService.update_player_diamond(kefuPlayer.getPlayerID(),kefuPlayerDiamond);
				//更新db
				playerService.updatePlayerDiamond(kefuPlayer.getPlayerID(), kefuPlayerDiamond,kefuPlayer.getVipExp());
				//记log
				String zhuanZhangRemark="转账给["+pl.getPlayerName()+",ID="+pl.getPlayerIndex()+"]"+(addPlayerDiamond)+"房卡";
				dbplayerService.createPlayerLog(kefuPlayer.getPlayerID(), kefuPlayer.getPlayerIndex(), kefuPlayer.getPlayerName(), kefuPlayerDiamond, LogConstant.OPERATION_TYPE_SUB_DIAMOND, 
						LogConstant.OPERATION_TYPE_KEFU_SEND_TO_OTHER_PLAYER_DIAMOND, addPlayerDiamond, zhuanZhangRemark, LogConstant.MONEY_TYPE_DIAMOND,Integer.parseInt(buyMoney),Integer.parseInt(buyGold),
						addPlayerDiamondRemark);

				//更新缓存
				kefuPlayer.setDiamond(kefuPlayerDiamond);
				dbplayerService.addPlayerToUidMapCache(kefuPlayer.getPlayerIndex(),GameConstant.GAME_ID, kefuPlayer);
			}
			
			//更新数据库
			
			if (null != playerService)
			{
				playerBean = playerService.getPlayerByID(playerID);
				if (playerBean == null) {
					this.errMsg="查询信息失败";
					return "kefuFailed";
				}
				this.setPlayerInfoOtherData();
				return "kefuDetial";
			}			
		}
		
		return "kefuFailed";
	}
	public boolean checkKefu(String userName)
	{
		ISystemConfigService cfgService = (ISystemConfigService) SpringService.getBean("sysConfigService");
		SystemConfigPara para=cfgService.getPara(GameConfigConstant.CONF_KEFU_ID_WHITE_LIST);
		if(para==null)
			return false;
		if(para.getValueStr().equals("")){
			return false;
		}
		List<String> ipList = new ArrayList<String>();
		String[] ips = para.getValueStr().split(";");
		for (int i = 0 ; i <ips.length ; i++ ) {
			if(ips[i].trim().equals(userName))
				return true;
	    } 
		return false;
	}
	//CHAOS_ZHANG **************************
	public String bindMembership()
	{
		//CHAOS_ZHANG web 请求
		HttpServletRequest request = ServletActionContext.getRequest();
		String playerId = request.getParameter("player_id");
		String memberId = request.getParameter("member_id");
		
		logger.info("bindMembership pid is : " + (playerId==null?"":playerId) + ", memberId is : " + (memberId==null?"":memberId));
		int retCode = 0;
		String retMsg = "";
//		if (!this.checkRechargeServerIp(request))
//		{
//			retMsg="ip白名单校验失败,ip : " + this.getIpAddr(request);
//		}		
		if (memberId==null || memberId.length() == 0)
		{
			retMsg += (retMsg==""?"请填写绑定ID":", 请填写绑定ID");
		}
		if(playerId == null || playerId.length() == 0)
		{
			retMsg += (retMsg==""?"玩家ID为空":", 玩家ID为空");
		}

		//CHAOS_ZHANG error status return
		if(retMsg.length() != 0)
		{
			logger.info("bindMembership pid:"+(playerId==null?"":playerId)+" param error, return msg:"+retMsg);
			retCode=1;
			this.errMsg="{\"errCode\":"+retCode+",\"errMsg\":\""+retMsg+"\"}";
			return "rechargereturn";
		}
		
		int iOpValue = Integer.parseInt(memberId);

		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");								
		IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
		if (null != dbplayerService && playerService!=null)
		{
			DailiPlayer stDailiPlayer = playerService.getDailiPlayerByPlayerIndex(iOpValue);
			if (stDailiPlayer == null)
			{
				retCode = 2;
				retMsg = "推荐人不存在，无法绑定";
				this.errMsg="{\"errCode\":"+retCode+",\"errMsg\":\""+retMsg+"\"}";
				return "rechargereturn";
			}
			
			Player pl = dbplayerService.getPlayerByPlayerIndex(Integer.parseInt(playerId));
			if (pl != null) 
			{
				if (pl.getSaveTime() > 0)
				{
					retMsg = "更新上级成功";
				}
				else
				{					
					retMsg = "congratulations, bind succeed";
				}
				
				pl.setSaveTime(iOpValue);
				dbplayerService.update_player_parent_index(pl);
				//更新用户redis信息
				String key = RedisKeyConstant.PLAYER_KEY + pl.PlayerGameId();
				String field = ""+pl.getPlayerIndex();
				JedisService jedis = (JedisService)SpringService.getBean("playerRedis");
				jedis.hset(key.getBytes(), field.getBytes(), pl.toDBPlayerBytes());
			}
			else
			{
				retCode = 4;
				retMsg = "不存在该用户";
			}											
		}
		else
		{
			logger.info("bindMembership pid:"+(playerId==null?"":playerId)+" system busy");
			retCode=4;
			retMsg="系统繁忙";
		}
		
		this.errMsg="{\"errCode\":"+retCode+",\"errMsg\":\""+retMsg+"\"}";
		return "rechargereturn";
	}
	//CHAOS_ZHANG **************************
	public String rechargePlayerDiamond() throws UnsupportedEncodingException
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		
		String playerId=request.getParameter("player_id");

		String rechargeDiamondStr=(request.getParameter("recharge_diamond"));

		String rechargeMoneyStr=(request.getParameter("recharge_money"));

		String remark = new String(request.getParameter("remark").getBytes("ISO-8859-1"),"UTF-8");

		String sign=request.getParameter("sign");

		String sendDiamondStr=request.getParameter("send_diamond");

		int gameId = Integer.parseInt(request.getParameter("gameid"));



		logger.info("auto_recharge pid:"+(playerId==null?"":playerId)+",rd:"+(rechargeDiamondStr==null?"":rechargeDiamondStr)+",rm:"+(rechargeMoneyStr==null?"":rechargeMoneyStr)+",remark:"+(remark==null?"":remark)+",sign:"+(sign==null?"":sign)+",sendDiamond:"+(sendDiamondStr==null?"":sendDiamondStr));
		int code=0;
		String msg=""; 
		if(!this.checkRechargeServerIp(request)){
			msg="ip白名单校验失败,ip:"+this.getIpAddr(request);
		}
		if(rechargeDiamondStr==null||rechargeDiamondStr.length() == 0 || Integer.parseInt(rechargeDiamondStr)==0){
			msg="请填写房卡";
		}
		if(rechargeMoneyStr==null||rechargeMoneyStr.length()== 0 ||Integer.parseInt(rechargeMoneyStr)==0){
			msg="请填写金额";			
		}		
		int sendDiamond=0;
		if(sendDiamondStr!=null&&sendDiamondStr.length()> 0 ){
			sendDiamond=Integer.parseInt(sendDiamondStr);
		}
		if(playerId==null||playerId.length() == 0){
			msg="玩家id不能为空";
		}
		
		try {
			if(!this.checkRechargeSign()){
				msg="签名验证失败";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(msg.length()!=0){
			logger.info("auto_recharge pid:"+(playerId==null?"":playerId)+" param error,msg:"+msg);
			code=1;
			this.errMsg="{\"errCode\":"+code+",\"errMsg\":\""+msg+"\"}";
			return "rechargereturn";
		}
		
			
		if(remark.length() == 0){
			remark = "";
		}
		int rechargeDiamond=Integer.parseInt(rechargeDiamondStr);
		int rechargeMoney=Integer.parseInt(rechargeMoneyStr);

		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");								
		IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");

		if (null != dbplayerService && playerService!=null)
		{	
			Player pl = dbplayerService.getPlayerByPlayerIndex(Integer.parseInt(playerId));
			if (pl != null) {
				logger.info("pl.getdiamond = " +pl.getDiamond() + "rechargeDiamond= "+rechargeDiamond + rechargeDiamondStr);
				int playerDiamond = pl.getDiamond()+rechargeDiamond;
				int playerOldDiamond = pl.getDiamond();
							
				//更新缓存
				dbplayerService.update_player_diamond(pl.getPlayerID(),playerDiamond);
				//更新db
				playerService.updatePlayerDiamond(pl.getPlayerID(), playerDiamond,pl.getVipExp());
				//记log
				String remark_log = "公众号自助充值,"+playerOldDiamond+"->"+playerDiamond;
				dbplayerService.createPlayerLog(pl.getPlayerID(), pl.getPlayerIndex(), pl.getPlayerName(), playerDiamond, LogConstant.OPERATION_TYPE_ADD_DIAMOND, 
						LogConstant.OPERATION_TYPE_AUTO_RECHARGE_GONGZHOGNHAO, rechargeDiamond, remark_log, LogConstant.MONEY_TYPE_DIAMOND,(rechargeMoney),(rechargeDiamond-sendDiamond),
						remark);
				
				//前面的更新缓存，可能在playermap中未找到，故在此处再次设置下钻石
				pl.setDiamond(playerDiamond);
				dbplayerService.addPlayerToUidMapCache(pl.getPlayerIndex(), gameId, pl);	
			} else {
				code = 2;
				msg = "不存在该用户";
			}
											
		}else{
			logger.info("auto_recharge pid:"+(playerId==null?"":playerId)+" system busy");
			code=1;
			msg="系统繁忙";
		}
		
		this.errMsg="{\"errCode\":"+code+",\"errMsg\":\""+msg+"\"}";
		return "rechargereturn";
	}

	public String rechargePlayer() throws UnsupportedEncodingException
	{
		HttpServletRequest request =  ServletActionContext.getRequest();

		String playerId=request.getParameter("player_id");
		String rechargeDiamondStr=(request.getParameter("recharge_diamond"));
		String rechargeMoneyStr=(request.getParameter("recharge_money"));
		String remark= new String(request.getParameter("remark").getBytes("ISO-8859-1"),"UTF-8");
//		String remark= URLDecoder.decode(request.getParameter("remark"),"UTF-8");
		String sign=request.getParameter("sign");
		String sendDiamondStr=request.getParameter("send_diamond");
		String rechargeTypeStr=request.getParameter("recharge_type");//1加钻石 2加金币
		String operationTypeStr=request.getParameter("operation_type");//operationType
		int gameId = Integer.parseInt(request.getParameter("gameid"));
		logger.info("auto_recharge_new pid:"+(playerId==null?"":playerId)+",rd:"+(rechargeDiamondStr==null?"":rechargeDiamondStr)+",rm:"+(rechargeMoneyStr==null?"":rechargeMoneyStr)+
				",remark:"+(remark==null?"":remark)+",sign:"+(sign==null?"":sign)+",sendDiamond:"+(sendDiamondStr==null?"":sendDiamondStr)+
				",recharge_type:"+(rechargeTypeStr==null?"":rechargeTypeStr)+",operation_type:"+(operationTypeStr==null?"":operationTypeStr)+
				",gameId:"+gameId);
		int code=0;
		String msg="";
		int rechargeType=Integer.parseInt(rechargeTypeStr);
		int operationType=Integer.parseInt(operationTypeStr);
		if(!this.checkRechargeServerIp(request)){
			msg="ip白名单校验失败,ip:"+this.getIpAddr(request);
		}
		if(!this.checkRechargeSignNew()){
			msg="签名验证失败";
		}
		if(Integer.parseInt(rechargeTypeStr)!=1 && Integer.parseInt(rechargeTypeStr)!=2){
			msg="充值类型错误";
		}
		if(rechargeDiamondStr==null||rechargeDiamondStr.length() == 0 || Integer.parseInt(rechargeDiamondStr)==0){
			msg="请填写房卡";
		}
		if(rechargeMoneyStr==null||rechargeMoneyStr.length()== 0){
			msg="请填写金额";
		}
		int sendDiamond=0;
		if(sendDiamondStr!=null&&sendDiamondStr.length()> 0 ){
			sendDiamond=Integer.parseInt(sendDiamondStr);
		}
		if(playerId==null||playerId.length() == 0){
			msg="玩家id不能为空";
		}

		if(remark.length() == 0){
			remark = "";
		}
		int rechargeDiamond=Integer.parseInt(rechargeDiamondStr);
		int rechargeMoney=Integer.parseInt(rechargeMoneyStr);
		if(rechargeDiamond<=0 || rechargeMoney<0 || sendDiamond<0||operationType<0){
			msg="参数错误";
		}

		if(msg.length()!=0){
			logger.info("auto_recharge_new pid:"+(playerId==null?"":playerId)+" param error,msg:"+msg);
			code=1;
			this.errMsg="{\"errCode\":"+code+",\"errMsg\":\""+msg+"\"}";
			return "rechargereturn";
		}
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
		if (null != dbplayerService && playerService!=null)
		{
			Player pl = dbplayerService.getPlayerByPlayerIndex(Integer.parseInt(playerId));
			if(pl == null){
				logger.info("auto_recharge_new pid:"+(playerId==null?"":playerId)+" get player null");
				code=1;
				msg="系统繁忙";
				this.errMsg="{\"errCode\":"+code+",\"errMsg\":\""+msg+"\"}";
				return "rechargereturn";
			}
			if(rechargeType==1){
				int playerDiamond = pl.getDiamond()+rechargeDiamond;
				int playerOldDiamond = pl.getDiamond();

				//更新缓存
				dbplayerService.update_player_diamond(pl.getPlayerID(),playerDiamond);
				//更新db
				playerService.updatePlayerDiamond(pl.getPlayerID(), playerDiamond,pl.getVipExp());
				//记log
				String remark_log = remark+playerOldDiamond+"->"+playerDiamond;
				dbplayerService.createPlayerLog(pl.getPlayerID(), pl.getPlayerIndex(), pl.getPlayerName(), playerDiamond, LogConstant.OPERATION_TYPE_ADD_DIAMOND,
						operationType, rechargeDiamond, remark_log, LogConstant.MONEY_TYPE_DIAMOND,(rechargeMoney),(rechargeDiamond-sendDiamond),
						"");
				//前面的更新缓存，可能在playermap中未找到，故在此处再次设置下钻石
				pl.setDiamond(playerDiamond);
				dbplayerService.addPlayerToUidMapCache(pl.getPlayerIndex(), gameId, pl);
			}else{
				int playerGold = pl.getGold()+rechargeDiamond;
				pl.setGold(playerGold);
				playerService.updatePlayerGold(pl.getPlayerID(), playerGold);

				//修改记录写入日志
				String remark_log_gold = remark+pl.getGold()+"->"+playerGold;
				dbplayerService.createPlayerLog(pl.getPlayerID(), pl.getPlayerIndex(), pl.getPlayerName(), playerGold, LogConstant.OPERATION_TYPE_ADD_GOLD,
						operationType, rechargeDiamond, remark_log_gold, LogConstant.MONEY_TYPE_GOLD);
			}

		}else{
			logger.info("auto_recharge_new pid:"+(playerId==null?"":playerId)+" system busy");
			code=1;
			msg="系统繁忙";
		}

		this.errMsg="{\"errCode\":"+code+",\"errMsg\":\""+msg+"\"}";
		return "rechargereturn";
	}

	public boolean checkRechargeSignNew() throws UnsupportedEncodingException
	{
		HttpServletRequest request =  ServletActionContext.getRequest();

		String playerId=request.getParameter("player_id");
		String rechargeDiamondStr=(request.getParameter("recharge_diamond"));
		String rechargeMoneyStr=(request.getParameter("recharge_money"));
//		String remark=request.getParameter("remark");
		String remark= new String(request.getParameter("remark").getBytes("ISO-8859-1"),"UTF-8");
		String sign=request.getParameter("sign").toLowerCase();
		String sendDiamondStr=request.getParameter("send_diamond");
		String rechargeType=request.getParameter("recharge_type");
		String operationTypeStr=request.getParameter("operation_type");//operationType
		String md5Str="player_id="+playerId+"recharge_diamond="+rechargeDiamondStr+"recharge_money="+rechargeMoneyStr
				+"remark="+remark+"send_diamond="+sendDiamondStr;

		String md5= MD5Service.encryptString( md5Str+"recharge_mdwl_1013").toLowerCase();
		if(!md5.equals(sign)){
			logger.info("new sign verify error paraStr:"+md5Str+"recharge_mdwl_1013,md5:"+md5+",sign:"+sign);
		}
		return md5.equals(sign);
	}

	public boolean checkRechargeServerIp(HttpServletRequest request)
	{
		ISystemConfigService cfgService = (ISystemConfigService) SpringService.getBean("sysConfigService");
		SystemConfigPara para=cfgService.getPara(GameConfigConstant.CONF_RECHARGE_SERVER_IP_WHITE_LIST);
		if(para==null)
			return false;
		if(para.getValueStr().equals("")){
			return false;
		}
		String serverIp = this.getIpAddr(request);
		logger.info("serverIp is : " + serverIp);
		List<String> ipList = new ArrayList<String>();
		String[] ips = para.getValueStr().split(";");
		for (int i = 0 ; i <ips.length ; i++ ) {
			if(ips[i].equals(serverIp))
				return true;
	    }
		return false;
	}
	
	public boolean checkRechargeSign() throws UnsupportedEncodingException
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		
		String playerId=request.getParameter("player_id");
		String rechargeDiamondStr=(request.getParameter("recharge_diamond"));
		String rechargeMoneyStr=(request.getParameter("recharge_money"));
		//String remark=request.getParameter("remark");
		String remark = new String(request.getParameter("remark").getBytes("ISO-8859-1"),"UTF-8");
		String sign=request.getParameter("sign").toLowerCase();
		String sendDiamondStr=request.getParameter("send_diamond");	
		String recharge_type = request.getParameter("recharge_type");
		String operation_type = request.getParameter("operation_type");
//		String md5Str="player_id="+playerId+"recharge_diamond="+rechargeDiamondStr+"recharge_money="+rechargeMoneyStr+"remark="+remark+"send_diamond="+sendDiamondStr+"recharge_type="+recharge_type+"operation_type="+operation_type;
		String md5Str="player_id="+playerId+"recharge_diamond="+rechargeDiamondStr+"recharge_money="+rechargeMoneyStr+"remark="+remark+"send_diamond="+sendDiamondStr;
		String md5= MD5Service.encryptString( md5Str+"recharge_mdwl_1013").toLowerCase();
		if(!md5.equals(sign)){
			logger.info("sign verify error paraStr:"+md5Str+"recharge_mdwl_1013,md5:"+md5+",sign:"+sign);
		}
		return md5.equals(sign);
	}
	
	public String getIpAddr(HttpServletRequest request) { 
	       String ip = request.getHeader("x-forwarded-for"); 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("WL-Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getRemoteAddr(); 
	       } 
	       return ip; 
	   } 
public String updatePlayerDiamond() {		
		
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		
		
		if(method.equals("POST"))
		{
			String playerID = request.getParameter("playerID");
			String diamond = request.getParameter("playerDiamond");
			String buyMoney = request.getParameter("buyMoney");
			String buyGold = request.getParameter("buyGold");
			String addPlayerDiamondRemark = request.getParameter("addPlayerDiamondRemark");
		
			if(!StringUtils.isInteger(diamond) || Integer.parseInt(diamond)<0){
				actionMsg = "房卡必须为1-11正整数字符!";
				return queryPlayerByAccount();
			}
			if(buyMoney.length() == 0){
				buyMoney = "0";
			}
			if(buyGold.length() == 0){
				buyGold = "0";
			}
			
			//更新缓存
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");	
			
			Player pl = dbplayerService.getPlayerByPlayerID(playerID);
			
			int plDiamond = pl.getDiamond();
			
			if (null != dbplayerService)
			{		
				if(pl!=null)
				{
					pl.setDiamond(Integer.parseInt(diamond));
					
					//修改记录写入日志
//					String detail = "" + baseAdminContext.getOssUser().getUsername()+"后台操作, " + plDiamond +"->" + diamond;
//					dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), pl.getGold(), LogConstant.OPERATION_TYPE_ADD_DIAMOND, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, Integer.parseInt(diamond), detail, LogConstant.MONEY_TYPE_DIAMOND);
				}
			}
			
			//更新数据库
			IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
			
			if (null != playerService)
			{
				//
				IDBServerPlayerService gamePlayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
				if(gamePlayerService!=null)
					gamePlayerService.update_player_diamond(playerID,Integer.parseInt(diamond));
				//
				playerService.updatePlayerDiamond(playerID, Integer.parseInt(diamond),pl.getVipExp());		
				
				//修改记录写入日志
				String detail = "" + baseAdminContext.getOssUser().getUsername()+"后台操作, " + plDiamond +"->" + diamond;
				dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), Integer.parseInt(diamond), LogConstant.OPERATION_TYPE_ADD_DIAMOND, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, Integer.parseInt(diamond)-plDiamond, detail, LogConstant.MONEY_TYPE_DIAMOND,Integer.parseInt(buyMoney),Integer.parseInt(buyGold),addPlayerDiamondRemark);
				
				playerBean = playerService.getPlayerByID(playerID);
				if (playerBean == null) {
					return "failed";
				}
				pl.setDiamond(Integer.parseInt(diamond));
				dbplayerService.addPlayerToUidMapCache(pl.getPlayerIndex(), GameConstant.GAME_ID, pl);
				return "detial";
			}			
		}
		
		return "failed";
	}
	public String kefuCloseVipTable()
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		String tableId = request.getParameter("tableId");
		IDBServerPlayerService	playerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		String un = baseAdminContext.getOssUser().getUsername();
		if(playerService != null)
		{
			logger.info(un+"=管理员清除桌子,table_id="+tableId);
			//
			GameTable gt = playerService.getVipTableByVipTableID(Integer.parseInt(tableId));
			if(gt!=null){
				playerService.clearOneTable(gt.getTableID());
				return "exchangeSuccess";
			}else{
				this.errMsg="桌子不存在";
			}
			
		}
		return "kefuFailed";
	}

	public String kefuUpdateParentPlayerIndex()
	{
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		if(this.checkKefu(baseAdminContext.getOssUser().getUsername()) == false){
			this.errMsg="没有操作权限，请联系管理员";
			return "kefuFailed";
		}
		String ans= this.updateParentPlayerIndex();
		return this.dealKefuReturn(ans);
	}

public String updateParentPlayerIndex() {		
	
	String method = ServletActionContext.getRequest().getMethod();
	HttpServletRequest request =  ServletActionContext.getRequest();
	HttpSession session = request.getSession();
	BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
	
	
	if(method.equals("POST"))
	{
		String playerID = request.getParameter("playerID");
		String parentPlayerIndex = request.getParameter("parentPlayerIndex");
		String parentVerifyTime = request.getParameter("parentVerifyTime");
		
		if((StringUtils.isInteger(parentPlayerIndex)==false || Integer.parseInt(parentPlayerIndex)<0)&&!parentPlayerIndex.equals("0")){
			actionMsg = "上级玩家索引必须为5-11正整数字符!";
			return "failed";
		}
		if(parentVerifyTime.length() == 0)
		{
			actionMsg = "群主认证日期不能为空！";
			return "failed";
		}
		
		 
		//更新缓存
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");	
		
		Player pl = dbplayerService.getPlayerByPlayerID(playerID);			
		
		if (null != dbplayerService)
		{		
			int idx=Integer.parseInt(parentPlayerIndex);
			Player parentPlayer=dbplayerService.getPlayerByPlayerIndex(idx);
			if(idx != 0 && parentPlayer==null)
			{
				actionMsg = "上级玩家索引错误，未找到该玩家!";
				return queryPlayerByAccount();
			}
			if(pl!=null)
			{				
//				pl.setSaveTime(idx);
				pl.setParentIndex(idx);
				pl.setParentVerifyTime(parentVerifyTime);
				dbplayerService.update_player_parent_index(pl);
				//修改记录写入日志
				String name ="";
				if (parentPlayer!=null){
					name = parentPlayer.getPlayerName();
				}
				String detail = "" + baseAdminContext.getOssUser().getUsername()+"=后台操作,修改群主["+pl.getPlayerIndex()+"]的上级为："+name+",parentidx="+idx;
				dbplayerService.createPlayerLog(playerID, pl.getPlayerIndex(), pl.getPlayerName(), pl.getGold(), LogConstant.OPERATION_TYPE_SET_PARENT_QUNZHU, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, 0, detail, LogConstant.MONEY_TYPE_DIAMOND);
			
				playerBean = dbplayerService.getPlayerByID(playerID);	
				this.setPlayerInfoOtherData();
				return "detial";
			}
		}			
	}
	
	return "failed";
}
public String kefuUpdatePlayerType()
{
	HttpServletRequest request =  ServletActionContext.getRequest();
	HttpSession session = request.getSession();
	String playerType = request.getParameter("playerType");
	if(!playerType.equals("0") && !playerType.equals("1") && !playerType.equals("3")){
		this.errMsg="玩家类型错误";
		return "kefuFailed";
	}
	BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
	if(this.checkKefu(baseAdminContext.getOssUser().getUsername()) == false){
		this.errMsg="没有操作权限，请联系管理员";
		return "kefuFailed";
	}
	String ans= this.updatePlayerType();
	return this.dealKefuReturn(ans);
}


	/**更新玩家类别*/
	public String updatePlayerType()
	{
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		
		
		if(method.equals("POST"))
		{
			String playerID = request.getParameter("playerID");
			String playerType = request.getParameter("playerType");
			String playerTypeRemark = request.getParameter("playerTypeRemark");
		
			if((!StringUtils.isInteger(playerType) || Integer.parseInt(playerType)<0)&&!playerType.equals("0")){
				actionMsg = "玩家类别必须为1-11正整数字符!";
				this.errMsg = "玩家类别必须为0-5整数字符!";
				return queryPlayerByAccount();
			}			
			
			
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
			IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
			
			//更新缓存
			Player pl = dbplayerService.getPlayerByPlayerIDFromCache(playerID);		
			
			if(pl != null)
			{
				String detail = baseAdminContext.getOssUser().getUsername()+"=修改玩家类型," + pl.getPlayerType() +"->" + playerType+",["+pl.getPlayerIndex()+"]";
				dbplayerService.createPlayerLog(playerID, (int)pl.getPlayerIndex(), pl.getPlayerName(), 0, LogConstant.OPERATION_TYPE_CHANGE_PLAYER_TYPE, LogConstant.OPERATION_TYPE_ADMIN_CHANGE_USER_DATA, Integer.parseInt(playerType), detail, LogConstant.MONEY_TYPE_OTHER,0,0,"");
				if(pl.getPlayerType()!=3 && playerType.equals("3")){//更新群主认证人
					IQunzhuPlayerDAO qpd =dbplayerService.getQunzhuPlayerDAO();
					qunzhuPlayerBean = qpd.getQunzhuByIndex(pl.getPlayerIndex());
					if(qunzhuPlayerBean == null){
						QunzhuPlayer qp = new QunzhuPlayer();						
						qp.setPlayerID(pl.getPlayerID());
						qp.setPlayerIndex(pl.getPlayerIndex());
						qpd.insert(qp);
					}
					
					qpd.updateAuthAdmin(baseAdminContext.getOssUser().getUsername(), pl.getPlayerIndex());
				}
				
				pl.setPlayerType(Integer.parseInt(playerType));
				pl.setPlayerTypeRemark(playerTypeRemark);				
				playerBean = pl;
			}
			//更新数据库
			dbplayerService.updatePlayerType(playerID, Integer.parseInt(playerType),playerTypeRemark);
			
			if(pl != null)
			{
				playerBean = pl;
			}
			else
			{
				playerBean = playerService.getPlayerByID(playerID);
				playerBean.setPlayerType(Integer.parseInt(playerType));
				playerBean.setPlayerTypeRemark(playerTypeRemark);
			}
			
			if (playerBean == null) {
				return "failed";
			}
			this.setPlayerInfoOtherData();
			return "detial";
		}
		
		return "failed";
	}
	
	/**清理玩家游戏信息*/
	public String clearplayerstation()
	{
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		
		
		if(method.equals("POST"))
		{
			String playerID = request.getParameter("playerID");
					
			
			//更新缓存
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");		
			
			if (null != dbplayerService)
			{				
				Player pl = dbplayerService.getPlayerByPlayerID(playerID);			
				if(pl!=null)
				{
					dbplayerService.clearPlayerStaion(pl);
					
					playerBean = pl;
					return "detial";
				}				
			}
		}
		
		return "failed";
	}
	
	
	public String showExchangePage()
	{
		return "exchangePage";
	}
	
	public String sumbmitExchangeGold()
	{
		//setErrorInfo("账号或密码错误");
		if(this.index <=0 || this.receiveIndex <= 0 || this.gold <= 0)
		{
			return "exchangePage";
		}
		
		//更新缓存
		IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
		IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
		
		Player pl = playerService.getPlayerByPlayerIndex(index);
		if(pl == null)
		{
			//找不到赠送玩家或者金币不足
			return "exchangePage";
		}
		
		if(pl.getGold() < this.gold)
		{
			return "exchangePage";
		}
		
		Player pl2 = playerService.getPlayerByPlayerIndex(this.receiveIndex);
		if(pl2 == null)
		{
			//找不到接收玩家
			return "exchangePage";
		}	
		
		//转出玩家
		Player sendPl = dbplayerService.getPlayerByPlayerID(pl.getPlayerID());
		if(sendPl == null)
		{
			return "exchangePage";
		}
		
		//转入玩家
		Player getPl = dbplayerService.getPlayerByPlayerID(pl2.getPlayerID());
		if(getPl == null)
		{
			return "exchangePage";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		BaseAdminContext baseAdminContext = (BaseAdminContext)(session.getAttribute(AdminSystemConstant.ADMIN_SYSTEM_SESSION_KEY));
		
		String remark = "管理员：" + baseAdminContext.getOssUser().getUsername() + "操作玩家" + this.index + "后台转赠给玩家：" + this.receiveIndex +",金币：" + this.gold;
		
		//减金币
		dbplayerService.sub_player_gold(sendPl, this.gold, LogConstant.OPERATION_TYPE_ADMIN_EXCHANGE_PLAYER_GOLD, remark);
		//加金币
		dbplayerService.add_player_gold(getPl, this.gold, LogConstant.OPERATION_TYPE_ADMIN_EXCHANGE_PLAYER_GOLD, remark);
		
		
		return "exchangeSuccess";
	}
	
	/**解除绑定手机号码*/
	public String clearPlayerPhoneNumber()
	{
		String method = ServletActionContext.getRequest().getMethod();
		HttpServletRequest request =  ServletActionContext.getRequest();
		
		
		if(method.equals("POST"))
		{
			String playerID = request.getParameter("playerID");			
		
			
			
			IDBServerPlayerService dbplayerService = (IDBServerPlayerService) SpringService.getBean("playerService");
			IPlayerService playerService = (IPlayerService)SpringService.getBean("playerBaseService");
			
			//更新缓存
			Player pl = dbplayerService.getPlayerByPlayerIDFromCache(playerID);
			if(pl != null)
			{
				pl.setPhoneNumber("");
				
				playerBean = pl;
			}
			
			//更新数据库
			dbplayerService.updatePlayerPhoneNumber(playerID, "");
			
			if(pl != null)
			{
				playerBean = pl;
			}
			else
			{
				playerBean = playerService.getPlayerByID(playerID);
				playerBean.setPhoneNumber("");
			}
			
			if (playerBean == null) {
				return "failed";
			}
			
			return "detial";
		}
		
		return "failed";
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

	public Player getPlayerBean() {
		return playerBean;
	}

	public void setPlayerBean(Player playerBean) {
		this.playerBean = playerBean;
	}
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getReceiveIndex() {
		return receiveIndex;
	}

	public void setReceiveIndex(int receiveIndex) {
		this.receiveIndex = receiveIndex;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getQueryPlayerType() {
		return queryPlayerType;
	}

	public void setQueryPlayerType(int queryPlayerType) {
		this.queryPlayerType = queryPlayerType;
	}
	
	public int getPayState() {
		return payState;
	}

	public void setPayState(int payState) {
		this.payState = payState;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public void setOnePageNum(Integer onePageNum) {
		this.onePageNum = onePageNum;
	}

	public Integer getCurrPageNO() {
		return currPageNO;
	}

	public void setCurrPageNO(Integer currPageNO) {
		this.currPageNO = currPageNO;
	}

	public List<PayHistory> getPayHistoryList() {
		return payHistoryList;
	}

	public void setPayHistoryList(List<PayHistory> payHistoryList) {
		this.payHistoryList = payHistoryList;
	}

	public String getQunZhuID() {
		return qunZhuID;
	}

	public void setQunZhuID(String qunZhuID) {
		this.qunZhuID = qunZhuID;
	}
	public String getErrMsg()
	{
		return this.errMsg;
	}
	public void setErrMsg(String msg)
	{
		this.errMsg=msg;
	}
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public QunzhuPlayer getQunzhuPlayerBean() {
		return qunzhuPlayerBean;
	}

	public void setQunzhuPlayerBean(QunzhuPlayer qunzhuPlayerBean) {
		this.qunzhuPlayerBean = qunzhuPlayerBean;
	}
	
	public String getQunzhuPhoneNumber() {
		return qunzhuPhoneNumber;
	}

	public void setQunzhuPhoneNumber(String qunzhuPhoneNumber) {
		this.qunzhuPhoneNumber = qunzhuPhoneNumber;
	}

	public String getQunzhuWxName() {
		return qunzhuWxName;
	}

	public void setQunzhuWxName(String qunzhuWxName) {
		this.qunzhuWxName = qunzhuWxName;
	}

	public String getQunzhuWxID() {
		return qunzhuWxID;
	}

	public void setQunzhuWxID(String qunzhuWxID) {
		this.qunzhuWxID = qunzhuWxID;
	}

	public String getQunzhuTrueName() {
		return qunzhuTrueName;
	}

	public void setQunzhuTrueName(String qunzhuTrueName) {
		this.qunzhuTrueName = qunzhuTrueName;
	}

	public String getQunzhuRemark() {
		return qunzhuRemark;
	}

	public void setQunzhuRemark(String qunzhuRemark) {
		this.qunzhuRemark = qunzhuRemark;
	}
}
