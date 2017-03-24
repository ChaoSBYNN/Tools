---
title: JSSDK 微信接口与 Java 结合
date: 2017-02-22 19:53:44
tags: [WeChat , Java]
---

# JSSDK微信接口 Java接入 转发分享信息

[TOC]

-------------------
><a href="http://dwz.cn/1KsMyS" target="_blank"> [ JSSDK文档 ]</a> 微信JS-SDK是微信公众平台面向网页开发者提供的基于微信内的网页开发工具包。   
> 
><a href="http://dwz.cn/3WfAJS" target="_blank">  [获取token文档 ]</a> JS-SDK获取access_token文档
>**ChaoS_Zhang** , 更多信息 , 具体请参考[GitHub][0].<a href="#"> [t7_chaos@163.com]</a>  .

##流程图

>流程图：

```flow
st=>start: 开始
e=>end: 结束
op1=>operation: 获取本地存储token
op2=>operation: 通过微信公众号 
				appid与secret 
				获取新的access_token
op3=>operation: 获取jsapi_ticket
cond1=>condition: 判断最近修改日期
是否不超过7200秒？
cond2=>condition: 判断获取的
				jsapi_ticket是否正常
				[异常情况:token过期]
st->op1->cond1->op3->cond2->e
op2->op3
cond1(no)->op2
cond1(yes)->op3
cond2(no)->op2
cond2(yes)->e
```


> **注意：**临界值操作，**临界值时虽然没有超过7200秒,但是token已经过期,需要重新获取**。


## 代码块
>###WXTokenServiceImpl 业务逻辑

``` java
	
	import java.io.UnsupportedEncodingException;
	import java.security.MessageDigest;
	import java.security.NoSuchAlgorithmException;
	import java.text.DateFormat;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.util.Date;
	import java.util.Formatter;
	import java.util.HashMap;
	import java.util.Map;
	import java.util.UUID;
	
	import javax.annotation.Resource;
	
	import org.springframework.stereotype.Service;
	
	import com.listenlives.dao.WXTokenDao;
	import com.listenlives.domain.WXToken;
	import com.listenlives.information.util.HttpHelper;
	
	import net.sf.json.JSONObject;
	
	/**
	 * @Author ChaoS_Zhang t7_chaos@163.com 
	 * @Date 2016年8月12日 上午11:00:02
	 * @Description  获取微信公众号有效期7200m access_token <br/>二次转发分享页面
	 * <br/>
	 * {@link http://dwz.cn/1KsMyS 微信JS-SDK说明文档}
	 */
	@SuppressWarnings("static-access")
	@Service
	public class WXTokenServiceImpl {
	
		/*
		 * toekn持久化
		 * updateWXToken(WXToken wxToken) 更新本地token<br/>
		 * selectWXToken() 获取本地token
		 * /
		@Resource
		private WXTokenDao wxTokenDao;
	
		public Map<String, String> getJSAPITicket(String url) {
	
			Map<String, String> resultMap = new HashMap<String, String>();
	
			/*
			 * 微信接口所需返回项
			 */
			String nonce_str = create_nonce_str();
			String timestamp = create_timestamp();
			String signature = "";
	
			String jsapiTicket = "";
			String assceeToken = "";
	
			try {
				WXToken wxToken = wxTokenDao.selectWXToken();
	
				assceeToken = wxToken.getAccessToken();
	
				if (compareWithExpiresIn(wxToken.getCreateTime())) {
	
					assceeToken = getNewAccessToken();
	
				}
	
				jsapiTicket = getNewJSAPITicket(assceeToken, 0).get("ticket").toString();
	
				/*
				 * 当前分享页面url 
				 * 注意这里参数名必须全部小写,且必须有序'''微信要求'''
				 */
				signature = "jsapi_ticket=" + jsapiTicket + 
									"&noncestr=" + nonce_str + 
									"&timestamp=" + timestamp + 
									"&url=" + url;
	
				MessageDigest crypt = MessageDigest.getInstance("SHA-1");
				crypt.reset();
				crypt.update(signature.getBytes("UTF-8"));
	
				signature = byteToHex(crypt.digest());
	
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			resultMap.put("url", url);
			resultMap.put("nonceStr", nonce_str);
			resultMap.put("timestamp", timestamp);
			resultMap.put("signature", signature);
	
			return resultMap;
		}
	
		/**
		 * @Description: 比较当前时间与上一次获取Token时间是否相差2小时 有效期7200秒
		 * {@link http://dwz.cn/3WfAJS 微信JSSDK access_token文档}
		 */
		public boolean compareWithExpiresIn(String createTime) {
	
			boolean flag = false;
	
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
			Date nowDate;
			Date tokenDate;
			long diff;
	
			try {
	
				nowDate = df.parse(df.format(new Date()));
				tokenDate = df.parse(createTime);
				
				diff = nowDate.getTime() - tokenDate.getTime();
				
				/*
				 * 微信2小时 7200m 变更token
				 */
				flag = diff > 7200000;
	
			} catch (ParseException e) {
				e.printStackTrace();
			}
	
			return flag;
		}
	
		private static String byteToHex(final byte[] hash) {
			Formatter formatter = new Formatter();
			for (byte b : hash) {
				formatter.format("%02x", b);
			}
			String result = formatter.toString();
			formatter.close();
			return result;
		}
	
		/**
		 * @Description: 生成签名的随机串
		 */
		private String create_nonce_str() {
			return UUID.randomUUID().toString();
		}
	
		/**
		 * @Description: 生成签名的时间戳
		 */
		private String create_timestamp() {
			return Long.toString(System.currentTimeMillis() / 1000);
		}
	
		public String getNewAccessToken() {
			String url = "https://api.weixin.qq.com/cgi-bin/token";
			String data = "";
			String result = "";
			try {
				Map<String, String> accessMap = new HashMap<String, String>();
				accessMap.put("grant_type", "client_credential");
				accessMap.put("appid", ${appid});
				accessMap.put("secret", ${secret});
				data = HttpHelper.post(accessMap, url);
				if (data != null && data != "") {
					JSONObject json = new JSONObject().fromObject(data);
					String access_token = (String) json.get("access_token");
					if (null != access_token && !"".equals(access_token)) {
	
						WXToken wxToken = new WXToken();
						wxToken.setAccessToken(access_token);
						wxTokenDao.updateWXToken(wxToken);
	
						result = access_token;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	
		public Map<String, Object> getNewJSAPITicket(String accessToken, int flag) {
			
			/*
			 * 避免死循环递归flag
			 */
			if (flag > 1) {
				return null;
			}
			
			String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
			String data = "";
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			try {
	
				Map<String, String> accessMap = new HashMap<String, String>();
				accessMap.put("access_token", accessToken);
				accessMap.put("type", "jsapi");
			
				data = HttpHelper.post(accessMap, url);
				
				if (data != null && data != "") {
					
					JSONObject json = new JSONObject().fromObject(data);
					String ticket = (String) json.get("ticket");
					if (null != ticket && !"".equals(ticket)) {
						resultMap.put("ticket", ticket);
					} else {
						resultMap = getNewJSAPITicket(getNewAccessToken(), flag + 1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultMap;
		}
	
	}

```
>###WXToken 微信公众号token实体
``` java
	/**
	 * @Author ChaoS_Zhang t7_chaos@163.com 
	 * @Date 2016年8月12日 上午10:55:28
	 */
	public class WXToken {
		
		private String id;
		
		private String accessToken;
		
		private String createTime;
		
		public String getId() {
			return id;
		}
	
		public void setId(String id) {
			this.id = id;
		}
	
		public String getAccessToken() {
			return accessToken;
		}
	
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
	
		public String getCreateTime() {
			return createTime;
		}
	
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
	
	}

```
 
  ---------

[0]: https://github.com/ChaoSBYNN
[1]: t7_chaos@163.com
