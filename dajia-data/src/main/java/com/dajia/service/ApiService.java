package com.dajia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import com.dajia.domain.Property;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.repository.PropertyRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiPingppUtils;
import com.dajia.util.ApiWdUtils;
import com.dajia.util.ApiWechatUtils;
import com.dajia.util.CommonUtils;
import com.dajia.vo.OrderVO;
import com.dajia.vo.ProductVO;
import com.dajia.vo.WechatArticleVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.api.KdtApiClient;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;

@Service
public class ApiService {

	Logger logger = LoggerFactory.getLogger("RpcLog");

	@Autowired
	private PropertyRepo propertyRepo;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	@Autowired
	EhCacheCacheManager ehcacheManager;

	public String loadApiWdToken() throws JsonParseException, JsonMappingException, IOException {
		String token = (propertyRepo.findByPropertyKey(ApiWdUtils.token)).propertyValue;
		boolean tokenValid = false;
		if (null != token && token.length() > 0) {
			String testTokenUrl = ApiWdUtils.testTokenUrl();
			String publicStr = ApiWdUtils.testTokenPublicStr(token);
			logger.info("testTokenUrl: " + testTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(testTokenUrl, String.class, publicStr);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			Integer returnCode = Integer.valueOf(((Map) map.get("status")).get("status_code").toString());
			if (null != returnCode && returnCode == ApiWdUtils.code_success) {
				tokenValid = true;
			}

		}
		if (!tokenValid) {
			logger.info("access token is invalid...");
			String appkey = (propertyRepo.findByPropertyKey(ApiWdUtils.appkey)).propertyValue;
			String secret = (propertyRepo.findByPropertyKey(ApiWdUtils.secret)).propertyValue;
			String generateTokenUrl = ApiWdUtils.generateTokenUrl(appkey, secret);
			logger.info("generateTokenUrl: " + generateTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(generateTokenUrl, String.class);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			Integer returnCode = Integer.valueOf(((Map) map.get("status")).get("status_code").toString());
			if (returnCode == 0) {
				String newToken = ((Map) map.get("result")).get("access_token").toString();
				logger.info("newToken: " + newToken);
				Property property = propertyRepo.findByPropertyKey(ApiWdUtils.token);
				property.propertyValue = newToken;
				propertyRepo.save(property);
				token = newToken;
			}
		}
		return token;
	}

	public String sendGet2Kdt(String method, HashMap<String, String> params) {
		String returnStr = "";
		KdtApiClient kdtApiClient;
		HttpResponse response;

		try {
			String appkey = (propertyRepo.findByPropertyKey(ApiKdtUtils.appkey)).propertyValue;
			String secret = (propertyRepo.findByPropertyKey(ApiKdtUtils.secret)).propertyValue;
			kdtApiClient = new KdtApiClient(appkey, secret);
			response = kdtApiClient.get(method, params);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
					"UTF-8"));
			StringBuffer resultSb = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				resultSb.append(line);
			}
			returnStr = resultSb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public Map<String, String> loadWechatUserInfo(String code) throws JsonParseException, JsonMappingException,
			IOException {
		String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
		String secret = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_secret).propertyValue;
		String requestTokenUrl = ApiWechatUtils.wechat_get_token_url + "?appid=" + appkey + "&secret=" + secret
				+ "&code=" + code + "&grant_type=authorization_code";
		logger.info("request token url: " + requestTokenUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(requestTokenUrl, String.class);
		logger.info("request token result: " + retrunJsonStr);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		map = mapper.readValue(retrunJsonStr, HashMap.class);
		String accessToken = "";
		String openId = "";
		if (null != map && map.containsKey("access_token") && map.containsKey("openid")) {
			accessToken = map.get("access_token").toString();
			openId = map.get("openid").toString();
			if (!accessToken.isEmpty() && !openId.isEmpty()) {
				String requestUserInfoUrl = ApiWechatUtils.wechat_get_userinfo_url + "?access_token=" + accessToken
						+ "&openid=" + openId + "&lang=zh_CN";
				logger.info("request userInfo url: " + requestUserInfoUrl);

				retrunJsonStr = ApiWechatUtils.httpGet(requestUserInfoUrl, "GET", "UTF-8");

				logger.info("request userInfo result: " + retrunJsonStr);
				map = mapper.readValue(retrunJsonStr, HashMap.class);
				return map;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getWechatOauthUrl(String refUserId, String productId, String refOrderId) {
		String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
		String oauthUrl = ApiWechatUtils.getOauthUrl(appkey, refUserId, productId, refOrderId);
		logger.info("oauthUrl: " + oauthUrl);
		return oauthUrl;
	}

	public String getWechatAccessToken() throws JsonParseException, JsonMappingException, IOException {
		String accessToken = null;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.global_cache_key);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key);
		if (null != cache.get(ApiWechatUtils.wechat_access_token_key)) {
			accessToken = cache.get(ApiWechatUtils.wechat_access_token_key).getObjectValue().toString();
		} else {
			String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
			String secret = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_secret).propertyValue;
			String requestAccessTokenUrl = ApiWechatUtils.wechat_get_access_token_url + "&appid=" + appkey + "&secret="
					+ secret;
			logger.info("request access token url: " + requestAccessTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(requestAccessTokenUrl, String.class);
			logger.info("request access token result: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			accessToken = map.get(ApiWechatUtils.wechat_access_token_key);
			cache.put(new Element(ApiWechatUtils.wechat_access_token_key, accessToken));
		}
		return accessToken;
	}

	public String getWechatJsapiTicket() throws JsonParseException, JsonMappingException, IOException {
		String ticket = null;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.global_cache_key);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key);
		if (null != cache.get(ApiWechatUtils.wechat_jsapi_key)) {
			ticket = cache.get(ApiWechatUtils.wechat_jsapi_key).getObjectValue().toString();
		} else {
			String accessToken = getWechatAccessToken();
			String requestTicketUrl = ApiWechatUtils.wechat_get_jsapi_ticket_url + "?access_token=" + accessToken
					+ "&type=jsapi";
			logger.info("request ticket url: " + requestTicketUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(requestTicketUrl, String.class);
			logger.info("request ticket result: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			ticket = map.get(ApiWechatUtils.wechat_jsapi_key);
			cache.put(new Element(ApiWechatUtils.wechat_jsapi_key, ticket));
		}
		return ticket;
	}

	public String getProductQrcodeUrl(Long productId) throws JsonParseException, JsonMappingException, IOException {
		String accessToken = getWechatAccessToken();
		String createQrcodeUrl = ApiWechatUtils.wechat_create_qrcode_url + "?access_token=" + accessToken;
		String paramStr = "{\"expire_seconds\": 10800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "
				+ productId + "}}}";
		logger.info("create qrcode url: " + createQrcodeUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.postForObject(createQrcodeUrl, paramStr, String.class);
		logger.info("create qrcode result: " + retrunJsonStr);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		map = mapper.readValue(retrunJsonStr, HashMap.class);
		String ticket = map.get(ApiWechatUtils.wechat_jsapi_key);
		String showQrcodeUrl = ApiWechatUtils.wechat_show_qrcode_url + "?ticket=" + ticket;
		return showQrcodeUrl;
	}

	public String getWechatSignature(String timestamp, String nonceStr, String url) {
		String ticket = null;
		try {
			ticket = getWechatJsapiTicket();
		} catch (Exception ex) {
			logger.error("get wechat sig error, timestamp={}, nonceStr={}, url={}, error={}", timestamp, nonceStr, url,
					ex.getMessage());
		}
		String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
		logger.info("wechat signature str: " + str);
		return DigestUtils.sha1Hex(str);
	}

	public Charge getPingppCharge(UserOrder order, User user, String channel) throws PingppException {
		String clientIp = null != user.lastVisitIP ? user.lastVisitIP : "127.0.0.1";
		Pingpp.apiKey = ApiPingppUtils.pingpp_live_key;
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("order_no", order.trackingId);
		Integer amt = order.actualPay.multiply(new BigDecimal(100)).intValue();
		chargeParams.put("amount", amt);// hard code as 1 during testing phase
		Map<String, String> app = new HashMap<String, String>();
		app.put("id", "app_DifDeLWjfrz9Wf9y");
		chargeParams.put("app", app);
		chargeParams.put("channel", channel);
		chargeParams.put("currency", "cny");
		chargeParams.put("client_ip", clientIp);
		chargeParams.put("subject", CommonUtils.subString(order.productDesc, 28));
		chargeParams.put("body", orderService.generateOrderInfoStr(order));
		if (channel.equalsIgnoreCase(CommonUtils.PayType.ALIPAY.getValue())) {
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("success_url", ApiWechatUtils.dajia_app_url + "#/tab/prog");
			extraParams.put("cancel_url", ApiWechatUtils.dajia_app_url);
			chargeParams.put("extra", extraParams);
		}
		if (channel.equalsIgnoreCase(CommonUtils.PayType.WECHAT.getValue())) {
			logger.info("-------charge open_id:" + user.oauthUserId);
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("open_id", user.oauthUserId);
			chargeParams.put("extra", extraParams);
		}
		Charge charge = Charge.create(chargeParams);
		logger.info("Ping++ Charge: " + charge.toString());
		return charge;
	}

	public Charge getPingppCharge(String chargeId, User user, String channel) throws PingppException {
		Pingpp.apiKey = ApiPingppUtils.pingpp_live_key;
		Map<String, Object> extraParams = new HashMap<String, Object>();
		if (channel.equalsIgnoreCase(CommonUtils.PayType.ALIPAY.getValue())) {
			extraParams.put("success_url", ApiWechatUtils.dajia_app_url + "#/tab/prog");
			extraParams.put("cancel_url", ApiWechatUtils.dajia_app_url);
		}
		if (channel.equalsIgnoreCase(CommonUtils.PayType.WECHAT.getValue())) {
			logger.info("-------charge open_id:" + user.oauthUserId);
			extraParams.put("open_id", user.oauthUserId);
		}
		Charge charge = Charge.retrieve(chargeId);
		charge.setChannel(channel);
		charge.setExtra(extraParams);
		return charge;
	}

	public Refund applyRefund(String chargeId, BigDecimal refundValue, String refundType) throws PingppException {
		Pingpp.apiKey = ApiPingppUtils.pingpp_live_key;
		Charge ch = Charge.retrieve(chargeId);
		if (null != ch) {
			Map<String, Object> refundMap = new HashMap<String, Object>();
			Integer amt = refundValue.multiply(new BigDecimal(100)).intValue();
			refundMap.put("amount", amt);// hard code as 1 during testing phase
			refundMap.put("description", refundType);
			Refund re = ch.getRefunds().create(refundMap);
			return re;
		} else {
			return null;
		}
	}

	public String getWechatEchoStr(Document doc) {
		String msgType = CommonUtils.getSingleValueFromXml(doc, "MsgType");
		String appId = CommonUtils.getSingleValueFromXml(doc, "ToUserName");
		String userOpenId = CommonUtils.getSingleValueFromXml(doc, "FromUserName");
		String eventKeyStr = CommonUtils.getSingleValueFromXml(doc, "EventKey");
		String content2User = "";
		String echoStr = "";

		if (null != msgType && msgType.equalsIgnoreCase("text")) {
			// 用户发送信息
			String contentFromUser = CommonUtils.getSingleValueFromXml(doc, "Content");
			if (null != contentFromUser) {
				if (contentFromUser.indexOf("1") == 0) {
					StringBuffer sb = new StringBuffer();
					sb.append("问：我先打价购买的话，会不会比较吃亏？").append("\n\n");
					sb.append("答：୧(⁼̴̶̤̀ω⁼̴̶̤́)૭当然不会啦~").append("\n");
					sb.append("所有商品的最终成交价格都以打价结束最后一件商品价格为准，差价之后会退还给您的哦~").append("\n");
					sb.append("比如：您购买某商品10：00并付款100元，11：00商品最终价格被打到90元，打价结束，那么我们会退还您10元。").append("\n");
					content2User = sb.toString();
				} else if (contentFromUser.indexOf("2") == 0) {
					StringBuffer sb = new StringBuffer();
					sb.append("问：什么是分享额外折扣？如何获得？").append("\n\n");
					sb.append("答：您打价购买某件商品后分享这件商品给朋友，只要朋友也通过分享链接成功购买，您即可获得此商品最终成交价的10%作为分享额外折扣~").append("\n");
					sb.append("成功邀请两位朋友可以获得20%额外折扣~~").append("\n");
					sb.append("如果邀请10位好友，就免单啦✧*｡٩(ˊᗜˋ*)و✧*").append("\n");
					sb.append(
							"邀请成功与否可以在&lt;a href=\"" + ApiWechatUtils.dajia_app_url
									+ "#/tab/prog\"&gt;打价实况&lt;/a&gt;查看哦！").append("\n");
					content2User = sb.toString();
				} else if (contentFromUser.indexOf("3") == 0) {
					StringBuffer sb = new StringBuffer();
					sb.append("问：商品何时发货？").append("\n\n");
					sb.append(
							"答：(•̀ω•́)✧正常情况下，打价网会在商品打价结束后的1-3天内发货哦~详细的物流跟踪可以在“我是打手-&lt;a href=\""
									+ ApiWechatUtils.dajia_app_url + "#/tab/mine/orders\"&gt;我的订单&lt;/a&gt;”里查看")
							.append("\n");
					content2User = sb.toString();
				} else if (contentFromUser.indexOf("4") == 0) {
					StringBuffer sb = new StringBuffer();
					sb.append("问：商品分享折扣何时到账？分享折扣退款到哪里？").append("\n\n");
					sb.append("答：(✪ω✪)商品分享折扣在打价结束后7-15个工作日内返还，退款到您支付时所用的银行账号里。").append("\n");
					content2User = sb.toString();
				} else if (contentFromUser.indexOf("5") == 0) {
					// 图文消息，特殊处理，直接返回。
					WechatArticleVO article = new WechatArticleVO();
					article.title = "打价网退换货政策";
					article.description = "一、  无理由退货政策1.       打价网承诺，对于您（作为消费者）通过打价网购买的商品，若商品能够保持";
					article.picUrl = "http://mmbiz.qpic.cn/mmbiz/0FVK0HOcKibcqLcna2d21AHB0xF3qCJ7nfK8MibKVpZV6oNhOr6ckFtetsMycYobm4iby10sXgKPiaxPiaT7utCxl3w/0?wx_fmt=jpeg";
					article.url = "http://mp.weixin.qq.com/s?__biz=MzAwNTg1MTI3Ng==&mid=100000002&idx=1&sn=37c18262836f113f26dc60215a96b9d4#rd";
					return generateWechatArticleReply(appId, userOpenId, article);
				} else {
					StringBuffer sb = new StringBuffer();
					sb.append("⊙０⊙遇到问题了？别着急，看看这里有没有您需要的~").append("\n\n");
					sb.append("回复【1】我先打价购买的话，会不会比较吃亏？").append("\n");
					sb.append("回复【2】什么是分享额外折扣？如何获得？").append("\n");
					sb.append("回复【3】商品何时发货？").append("\n");
					sb.append("回复【4】分享额外折扣何时到账？退款到哪里？").append("\n");
					sb.append("回复【5】如何进行退换货？").append("\n\n");
					sb.append("如果您的问题不在此列，请戳左下的小键盘转换成聊天窗口，把您的问题告诉给我们，工作人员会尽快给您满意的答复哦，谢谢٩(๑ᵒ̴̶̷͈᷄ᗨᵒ̴̶̷͈᷅)").append("\n");
					content2User = sb.toString();
				}
			}
		} else if (null != msgType && msgType.equalsIgnoreCase("event")) {
			String eventStr = CommonUtils.getSingleValueFromXml(doc, "Event");
			// 用户关注公众号
			if (null != eventStr && eventStr.equalsIgnoreCase("subscribe")) {
				StringBuffer sb = new StringBuffer();
				sb.append("欢迎来打价~！").append("\n");
				sb.append("每晚8点准时上新~！/坏笑").append("\n");
				content2User = sb.toString();
				// 带参数的二维码扫描关注
				if (null != eventKeyStr) {
					String productId = ApiWechatUtils.removeQrPrefix(eventKeyStr);
					return generateArticleForProduct(appId, userOpenId, productId);
				}
			}
			// 已关注用户扫描带参数二维码
			else if (null != eventStr && eventStr.equalsIgnoreCase("SCAN")) {
				String productId = eventKeyStr;
				return generateArticleForProduct(appId, userOpenId, productId);
			}
			// 用户点击菜单
			else if (null != eventStr && eventStr.equalsIgnoreCase("CLICK")) {
				if (eventKeyStr.equalsIgnoreCase(ApiWechatUtils.wechat_menu_001)) {
					// 常见问题
					StringBuffer sb = new StringBuffer();
					sb.append("⊙０⊙遇到问题了？别着急，看看这里有没有您需要的~").append("\n\n");
					sb.append("回复【1】我先打价购买的话，会不会比较吃亏？").append("\n");
					sb.append("回复【2】什么是分享额外折扣？如何获得？").append("\n");
					sb.append("回复【3】商品何时发货？").append("\n");
					sb.append("回复【4】分享额外折扣何时到账？退款到哪里？").append("\n");
					sb.append("回复【5】如何进行退换货？").append("\n\n");
					sb.append("如果您的问题不在此列，请戳左下的小键盘转换成聊天窗口，把您的问题告诉给我们，工作人员会尽快给您满意的答复哦，谢谢٩(๑ᵒ̴̶̷͈᷄ᗨᵒ̴̶̷͈᷅)").append("\n");
					content2User = sb.toString();
				} else if (eventKeyStr.equalsIgnoreCase(ApiWechatUtils.wechat_menu_002)) {
					// 自助退货
					StringBuffer sb = new StringBuffer();
					sb.append("(•̀ω•́)✧请先按左下角小键盘按钮，然后再按以下格式内容给客服留言~提交退货申请，审核成功后，客服会把退货地址发送给您，如不在退货规则内，也会告知您~").append(
							"\n\n");
					sb.append("购买日期：20150101").append("\n");
					sb.append("订单号码：XXXXXXXX").append("\n");
					sb.append("退货原因：文字文字文字").append("\n");
					content2User = sb.toString();
				} else if (eventKeyStr.equalsIgnoreCase(ApiWechatUtils.wechat_menu_003)) {
					// 在线留言
					StringBuffer sb = new StringBuffer();
					sb.append("请戳左下的小键盘转换成聊天窗口，把您的问题告诉给我们，工作人员会尽快给您满意的答复哦，谢谢٩(๑ᵒ̴̶̷͈᷄ᗨᵒ̴̶̷͈᷅)و").append("\n");
					content2User = sb.toString();
				} else if (eventKeyStr.equalsIgnoreCase(ApiWechatUtils.wechat_menu_004)) {
					// 商务合作
					StringBuffer sb = new StringBuffer();
					sb.append("如您有意向与打价网共同发展，请发邮件至：postmaster@51daja.bid或直接在公众号留言，我们会尽快给您回复~").append("\n\n");
					content2User = sb.toString();
				}
			}
		}
		echoStr = generateWechatTxtReply(appId, userOpenId, content2User);
		return echoStr;
	}

	public void sendWechatTemplateMsg(String templateId, String userOpenId, String trackingId)
			throws JsonParseException, JsonMappingException, IOException {
		String accessToken = getWechatAccessToken();
		String sendTemplateMsgUrl = ApiWechatUtils.wechat_send_template_msg_url + "?access_token=" + accessToken;
		logger.info("send template msg url: " + sendTemplateMsgUrl);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", userOpenId);
		map.put("template_id", templateId);
		map.put("url", getMsgUrl(templateId));
		map.put("data", getMsgDataMap(templateId, trackingId));
		ObjectMapper mapper = new ObjectMapper();
		String postContent = mapper.writeValueAsString(map);
		logger.info("send template msg content: " + postContent);

		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.postForObject(sendTemplateMsgUrl, postContent, String.class);
		logger.info("send template msg result: " + retrunJsonStr);
	}

	private Map<String, Object> getMsgDataMap(String templateId, String trackingId) {
		Map<String, Object> map = new HashMap<>();
		if (templateId.equalsIgnoreCase(ApiWechatUtils.wechat_msg_template_order_success)) {
			OrderVO orderVO = orderService.getOrderDetailByTrackingId(trackingId);

			Map<String, Object> nameMap = new HashMap<>();
			nameMap.put("value", orderVO.productDesc);
			map.put("name", nameMap);

			Map<String, Object> remarkMap = new HashMap<>();
			remarkMap.put("value", "支付金额：" + orderVO.actualPay + "元");
			map.put("remark", remarkMap);
		} else if (templateId.equalsIgnoreCase(ApiWechatUtils.wechat_msg_template_refund_success)) {
			map.put("first", "");
			map.put("reason", "");
			map.put("refund", "");
			map.put("remark", "");
		}
		return map;
	}

	private String getMsgUrl(String templateId) {
		String msgUrl = "";
		if (templateId.equalsIgnoreCase(ApiWechatUtils.wechat_msg_template_order_success)) {
			msgUrl = ApiWechatUtils.dajia_app_url + "#/tab/prog";
		} else if (templateId.equalsIgnoreCase(ApiWechatUtils.wechat_msg_template_refund_success)) {
			msgUrl = ApiWechatUtils.dajia_app_url + "#/tab/prog";
		}
		return msgUrl;
	}

	private String generateWechatTxtReply(String appId, String userOpenId, String content) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<ToUserName>").append(userOpenId).append("</ToUserName>");
		sb.append("<FromUserName>").append(appId).append("</FromUserName>");
		sb.append("<CreateTime>").append(System.currentTimeMillis()).append("</CreateTime>");
		sb.append("<MsgType>").append("text").append("</MsgType>");
		sb.append("<Content>").append(content).append("</Content>");
		sb.append("</xml>");
		return sb.toString();
	}

	private String generateWechatArticleReply(String appId, String userOpenId, WechatArticleVO article) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<ToUserName>").append(userOpenId).append("</ToUserName>");
		sb.append("<FromUserName>").append(appId).append("</FromUserName>");
		sb.append("<CreateTime>").append(System.currentTimeMillis()).append("</CreateTime>");
		sb.append("<MsgType>").append("news").append("</MsgType>");
		sb.append("<ArticleCount>1</ArticleCount>");
		sb.append("<Articles>");
		sb.append("<item>");
		sb.append("<Title>").append(article.title).append("</Title> ");
		sb.append("<Description>").append(article.description).append("</Description>");
		sb.append("<PicUrl>").append(article.picUrl).append("</PicUrl>");
		sb.append("<Url>").append(article.url).append("</Url>");
		sb.append("</item>");
		sb.append("</Articles>");
		sb.append("</xml>");
		return sb.toString();
	}

	private String generateArticleForProduct(String appId, String userOpenId, String productId) {
		logger.info("qrscene: " + productId);
		ProductVO productVO = productService.loadProductDetail(Long.valueOf(productId));
		if (null == productVO) {
			return null;
		}
		WechatArticleVO article = new WechatArticleVO();
		article.title = "点击前往购买您关注的产品";
		article.description = productVO.name + " " + productVO.currentPrice + "元";
		article.picUrl = productVO.imgUrl;
		article.url = ApiWechatUtils.dajia_app_url + "#/tab/prod/" + productId;
		return generateWechatArticleReply(appId, userOpenId, article);
	}
}
