package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ProgressVO implements Comparable<ProgressVO> {

	public String progressType;

	public Long orderId;

	public Long productId;
	
	public Long productItemId;

	public Integer orderQuantity;

	public BigDecimal priceOff;

	public String orderUserName;

	public Date orderDate;

	public int compareTo(ProgressVO o) {
		return this.orderDate.compareTo(o.orderDate);
	}
}
