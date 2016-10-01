package com.dajia.vo;

import java.util.Date;
import java.util.List;

import com.dajia.domain.UserContact;

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

	public Date lastVisitDate;

	public String lastVisitIP;

	public Date createdDate;

	public String isAdmin;

	public String isSales;

	public UserContact userContact;

	public List<UserContact> userContacts;
}
