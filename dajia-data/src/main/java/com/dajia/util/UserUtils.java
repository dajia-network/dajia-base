package com.dajia.util;

import com.dajia.domain.User;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.SalesVO;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

	public static String session_user = "user";

	public static String generateUserName(String key) {
		String userName = "用户" + String.valueOf(key);
		return userName;
	}

	public static void copyUserProperties(LoginUserVO src, User target) {
		target.email = src.email;
		target.mobile = src.mobile;
		if (null != src.userName) {
			target.userName = src.userName;
		}
		target.password = src.password;
		// target.lastVisitDate = src.loginDate;
		// target.lastVisitIP = src.loginIP;
	}

	public static void copyUserProperties(User src, LoginUserVO target) {
		target.email = src.email;
		target.mobile = src.mobile;
		target.userId = src.userId;
		if (null != src.userName) {
			target.userName = src.userName;
		}
	}

	public static LoginUserVO addLoginSession(LoginUserVO loginUser, User user, HttpServletRequest request) {
		if (null == user) {
			return null;
		}
		if (null == loginUser) {
			loginUser = new LoginUserVO();
		}
		loginUser.userId = user.userId;
		loginUser.mobile = user.mobile;
		loginUser.userName = user.userName;
		loginUser.password = user.password;
		loginUser.oauthType = user.oauthType;
		loginUser.oauthUserId = user.oauthUserId;
		loginUser.headImgUrl = user.headImgUrl;
		loginUser.isAdmin = user.isAdmin;
		loginUser.isSales = user.isSales;
		request.getSession().setAttribute(UserUtils.session_user, loginUser);
		return loginUser;
	}

	public static LoginUserVO getUserVO(User user) {
		if (null == user) {
			return null;
		}
		LoginUserVO loginUser = new LoginUserVO();
		loginUser.userId = user.userId;
		loginUser.mobile = user.mobile;
		loginUser.sex = user.sex;
		loginUser.country = user.country;
		loginUser.province = user.province;
		loginUser.city = user.city;
		loginUser.userName = user.userName;
		loginUser.password = user.password;
		loginUser.oauthType = user.oauthType;
		loginUser.oauthUserId = user.oauthUserId;
		loginUser.headImgUrl = user.headImgUrl;
		loginUser.isAdmin = user.isAdmin;
		loginUser.isSales = user.isSales;
		loginUser.createdDate = user.createdDate;
		loginUser.lastVisitDate = user.lastVisitDate;
		loginUser.lastVisitIP = user.lastVisitIP;
		if (loginUser.sex.equalsIgnoreCase("1")) {
			loginUser.sex4Show = "男";
		} else if (loginUser.sex.equalsIgnoreCase("2")) {
			loginUser.sex4Show = "女";
		} else {
			loginUser.sex4Show = "未知";
		}
		StringBuilder location4Show = new StringBuilder();
		if (null != loginUser.country && loginUser.country.length() > 0) {
			location4Show.append(loginUser.country);
		}
		if (null != loginUser.province && loginUser.province.length() > 0) {
			location4Show.append(" ");
			location4Show.append(loginUser.province);
		}
		if (null != loginUser.city && loginUser.city.length() > 0) {
			location4Show.append(" ");
			location4Show.append(loginUser.city);
		}
		loginUser.location4Show = location4Show.toString();
		return loginUser;
	}

	public static SalesVO getSalesVO(User user) {
		if (null == user) {
			return null;
		}
		SalesVO sales = new SalesVO();
		sales.userId = user.userId;
		sales.mobile = user.mobile;
		sales.sex = user.sex;
		sales.country = user.country;
		sales.province = user.province;
		sales.city = user.city;
		sales.userName = user.userName;
		sales.headImgUrl = user.headImgUrl;
		sales.isAdmin = user.isAdmin;
		sales.isSales = user.isSales;
		sales.createdDate = user.createdDate;
		sales.lastVisitDate = user.lastVisitDate;
		sales.lastVisitIP = user.lastVisitIP;
		if (sales.sex.equalsIgnoreCase("1")) {
			sales.sex4Show = "男";
		} else if (sales.sex.equalsIgnoreCase("2")) {
			sales.sex4Show = "女";
		} else {
			sales.sex4Show = "未知";
		}
		StringBuilder location4Show = new StringBuilder();
		if (null != sales.country && sales.country.length() > 0) {
			location4Show.append(sales.country);
		}
		if (null != sales.province && sales.province.length() > 0) {
			location4Show.append(" ");
			location4Show.append(sales.province);
		}
		if (null != sales.city && sales.city.length() > 0) {
			location4Show.append(" ");
			location4Show.append(sales.city);
		}
		sales.location4Show = location4Show.toString();
		return sales;
	}
}