package com.dajia.util;

public class ApiUpyunUtils {
	public static final String server_url = "http://v0.api.upyun.com/";
	public static final String app_img_folder = "dajia-static/product_img/";
	public static final String app_img_domain = "http://dajia-static.b0.upaiyun.com/product_img/";
	public static final String upyun_username_key = "upyun_username";
	public static final String upyun_password_key = "upyun_password";

	public static String generateFileName(String originalFileName) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(String.valueOf(System.currentTimeMillis()));
		fileName.append("_");
		fileName.append(EncodingUtil.encode("MD5", originalFileName));
		return fileName.toString();
	}
}