package com.dajia.util;

import java.security.MessageDigest;

public class EncodingUtil {

	public static String salt = "daj1a";

	public static String encode(String algorithm, String str) {
		if (str == null) {
			return null;
		}
		try {
			str += salt;
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes("UTF8"));
			return convertToHexString(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static String convertToHexString(byte data[]) {
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			strBuffer.append(Integer.toHexString(0xff & data[i]));
		}
		return strBuffer.toString();
	}

	public static void main(String[] args) {
		System.out.println("abcd MD5  :" + EncodingUtil.encode("MD5", "abcd"));
		System.out.println("abcd SHA1 :" + EncodingUtil.encode("SHA1", "abcd"));
	}

}