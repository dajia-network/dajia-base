package com.dajia.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dajia.domain.UserContact;
import com.google.common.collect.Maps;

public class LoginUserVO {
	public Long userId;

	public String userName;

	public String password;

	public String email;

	public String mobile;

	public String sex;

	public String sex4Show;

	public String country;

	public String province;

	public String city;

	public String location4Show;

	public String oauthType;

	public String oauthUserId;

	public String headImgUrl;

	public String signupCode;

	public String signinCode;

	public String bindingCode;

	public Date loginDate;

	public String loginIP;
	/**
	 * 登录方式 比如 mobile 或者 userPass
	 */
	public String loginType;

	public Date lastVisitDate;

	public String lastVisitIP;

	public Date createdDate;

	public String isAdmin;

	public String isSales;

	/**
	 * 扩展字段 存放例如淘宝滑动验证码的token等数据
	 */
	public Map<String, String> extraInfo = Maps.newHashMap();

	public UserContact userContact;

	public List<UserContact> userContacts;

	@Override
	public String toString() {
		return "LoginUserVO{" +
				"userId=" + userId +
				", userName='" + userName + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", mobile='" + mobile + '\'' +
				", sex='" + sex + '\'' +
				", sex4Show='" + sex4Show + '\'' +
				", country='" + country + '\'' +
				", province='" + province + '\'' +
				", city='" + city + '\'' +
				", location4Show='" + location4Show + '\'' +
				", oauthType='" + oauthType + '\'' +
				", oauthUserId='" + oauthUserId + '\'' +
				", headImgUrl='" + headImgUrl + '\'' +
				", signupCode='" + signupCode + '\'' +
				", signinCode='" + signinCode + '\'' +
				", bindingCode='" + bindingCode + '\'' +
				", loginDate=" + loginDate +
				", loginIP='" + loginIP + '\'' +
				", loginType='" + loginType + '\'' +
				", lastVisitDate=" + lastVisitDate +
				", lastVisitIP='" + lastVisitIP + '\'' +
				", createdDate=" + createdDate +
				", isAdmin='" + isAdmin + '\'' +
				", isSales='" + isSales + '\'' +
				", extraInfo=" + extraInfo +
				", userContact=" + userContact +
				", userContacts=" + userContacts +
				'}';
	}
}
