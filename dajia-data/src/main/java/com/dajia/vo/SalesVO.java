package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SalesVO {
	public Long userId;

	public String userName;

	public String email;

	public String mobile;

	public String sex;

	public String sex4Show;

	public String country;

	public String province;

	public String city;

	public String location4Show;

	public String headImgUrl;

	public Date lastVisitDate;

	public String lastVisitIP;

	public Date createdDate;

	public String isAdmin;

	public String isSales;

	public Integer refUserNumMTD;

	public Integer refOrderNumMTD;

	public BigDecimal refAmountMTD;

	public BigDecimal bonusAmountMTD;

	public Integer refUserNumWTD;

	public Integer refOrderNumWTD;

	public BigDecimal refAmountWTD;

	public BigDecimal bonusAmountWTD;

	public List<SalesIndicatorVO> salesIndicators;
}
