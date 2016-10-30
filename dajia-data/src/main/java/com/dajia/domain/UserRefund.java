package com.dajia.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Real refund info. Only be saved when refund really happen.
 * 
 * @author Puffy
 */
@Entity
@Table(name = "user_refund")
public class UserRefund extends BaseModel {

	@Column(name = "refund_id")
	@Id
	@GeneratedValue
	public Long refundId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "product_id")
	public Long productId;

	@Column(name = "product_item_id")
	public Long productItemId;

	@Column(name = "order_id")
	public Long orderId;

	@Column(name = "refund_date")
	public Date refundDate;

	@Column(name = "refund_value")
	public BigDecimal refundValue;

	@Column(name = "refund_type")
	public Integer refundType;

	@Column(name = "refund_status")
	public Integer refundStatus;

	@Transient
	public String refundType4Show;

	@Transient
	public String refundStatus4Show;

	@Column(name = "api_msg")
	public String apiMsg;

	@Override
	public String toString() {
		return "UserRefund{" +
				"refundId=" + refundId +
				", userId=" + userId +
				", productId=" + productId +
				", productItemId=" + productItemId +
				", orderId=" + orderId +
				", refundDate=" + refundDate +
				", refundValue=" + refundValue +
				", refundType=" + refundType +
				", refundStatus=" + refundStatus +
				", refundType4Show='" + refundType4Show + '\'' +
				", refundStatus4Show='" + refundStatus4Show + '\'' +
				", apiMsg='" + apiMsg + '\'' +
				'}';
	}
}