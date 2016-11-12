package com.dajia.util;

import com.dajia.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ApiWechatUtils {

	private final static Logger logger = LoggerFactory.getLogger(ApiWechatUtils.class);

	public static final String wechat_api_token = "dajia";
	public static final String wechat_get_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String wechat_get_userinfo_url = "https://api.weixin.qq.com/sns/userinfo";
	public static final String wechat_get_access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
	public static final String wechat_get_jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	public static final String wechat_app_key = "appkey_wechat";
	public static final String wechat_secret = "secret_wechat";
	public static final String wechat_oauth_type = "Wechat";
	public static final String wechat_oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String wechat_callback_url = "http%3A%2F%2F51daja.com%2Fwechatoauth";
	public static final String wechat_access_token_key = "access_token";
	public static final String wechat_jsapi_key = "ticket";
	public static final String wechat_create_qrcode_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
	public static final String wechat_show_qrcode_url = "https://mp.weixin.qq.com/cgi-bin/showqrcode";

	public static void updateWechatUserInfo(User user, Map<String, String> userInfoMap) {
		user.userName = userInfoMap.get("nickname");
		user.headImgUrl = userInfoMap.get("headimgurl");
		user.sex = String.valueOf(userInfoMap.get("sex"));
		user.country = userInfoMap.get("country");
		user.province = userInfoMap.get("province");
		user.city = userInfoMap.get("city");
	}

	public static String getOauthUrl(String appId, String refUserId, String productId, String refOrderId) {
		String url = "";
		if (!CommonUtils.checkParameterIsNull(refUserId)) {
			if (!CommonUtils.checkParameterIsNull(refOrderId)) {
				url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
						+ "&response_type=code&scope=snsapi_userinfo&state=" + refUserId + "_" + productId + "_"
						+ refOrderId + "#wechat_redirect";
			} else {
				url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
						+ "&response_type=code&scope=snsapi_userinfo&state=" + refUserId + "_" + productId
						+ "#wechat_redirect";
			}
		} else if (!CommonUtils.checkParameterIsNull(productId)) {
			url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
					+ "&response_type=code&scope=snsapi_userinfo&state=" + productId + "#wechat_redirect";
		} else {
			url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		}
		return url;
	}

	public static String httpGet(String url, String method, String responseEncoding) {
		try {
			URL innerURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) innerURL.openConnection();
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(3000);

			conn.connect();
			logger.info("[test] connected");
			BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			int k;

			while ((k = inputStream.read()) != -1) {
				outputStream.write(k);
			}

			outputStream.flush();
			logger.info("[test] output flushed, total {} bytes", outputStream.toByteArray().length);
			inputStream.close();
			conn.disconnect();
			logger.info("[test] disconnected");
			return new String(outputStream.toByteArray(), responseEncoding);
		} catch (Exception e) {
			logger.error("http get error for url {}", url, e);
			return null;
		}
	}
}