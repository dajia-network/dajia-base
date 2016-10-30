package com.dajia.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_order")
public class UserOrder extends BaseModel {

	@Column(name = "order_id")
	@Id
	@GeneratedValue
	public Long orderId;

	@Column(name = "tracking_id")
	public String trackingId;

	@Column(name = "product_id", nullable = false)
	public Long productId;

	@Column(name = "product_item_id", nullable = false)
	public Long productItemId;

	@Column(name = "product_desc")
	public String productDesc;

	@Column(name = "product_shared")
	public String productShared;

	@Column(name = "user_id", nullable = false)
	public Long userId;

	@Column(name = "ref_user_id")
	public Long refUserId;

	@Column(name = "ref_order_id")
	public Long refOrderId;

	@Column(name = "payment_id", nullable = false)
	public String paymentId;

	@Column(name = "quantity")
	public Integer quantity;

	@Column(name = "order_status")
	public Integer orderStatus;

	@Column(name = "pay_type")
	public Integer payType;

	@Column(name = "unit_price")
	public BigDecimal unitPrice;

	@Column(name = "total_price")
	public BigDecimal totalPrice;

	@Column(name = "post_fee")
	public BigDecimal postFee;

	@Column(name = "order_date")
	public Date orderDate;

	@Column(name = "deliver_date")
	public Date deliverDate;

	@Column(name = "close_date")
	public Date closeDate;

	@Column(name = "logistic_agent")
	public String logisticAgent;

	@Column(name = "logistic_tracking_id")
	public String logisticTrackingId;

	@Column(name = "contact_name", nullable = false)
	public String contactName;

	@Column(name = "contact_mobile", nullable = false)
	public String contactMobile;

	@Column(name = "address")
	public String address;

	@Column(name = "pingxx_charge")
	public String pingxxCharge;

	@Column(name = "comments")
	public String comments;

	@Column(name = "user_comments")
	public String userComments;

	@Column(name = "admin_comments")
	public String adminComments;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userOrder", fetch = FetchType.EAGER)
	public List<UserOrderItem> orderItems;

	/**
	 * 这个订单消耗的优惠券列表
	 */
	@Column(name = "user_coupon_ids")
	public String userCouponIds;

	/**
	 * 本订单实付的价格
	 */
	@Column(name = "actual_pay")
	public BigDecimal actualPay;

	@Override
	public String toString() {
		return "UserOrder{" +
				"orderId=" + orderId +
				", trackingId='" + trackingId + '\'' +
				", productId=" + productId +
				", productItemId=" + productItemId +
				", productDesc='" + productDesc + '\'' +
				", productShared='" + productShared + '\'' +
				", userId=" + userId +
				", refUserId=" + refUserId +
				", refOrderId=" + refOrderId +
				", paymentId='" + paymentId + '\'' +
				", quantity=" + quantity +
				", orderStatus=" + orderStatus +
				", payType=" + payType +
				", unitPrice=" + unitPrice +
				", totalPrice=" + totalPrice +
				", postFee=" + postFee +
				", orderDate=" + orderDate +
				", deliverDate=" + deliverDate +
				", closeDate=" + closeDate +
				", logisticAgent='" + logisticAgent + '\'' +
				", logisticTrackingId='" + logisticTrackingId + '\'' +
				", contactName='" + contactName + '\'' +
				", contactMobile='" + contactMobile + '\'' +
				", address='" + address + '\'' +
				", pingxxCharge='" + pingxxCharge + '\'' +
				", comments='" + comments + '\'' +
				", userComments='" + userComments + '\'' +
				", adminComments='" + adminComments + '\'' +
				", orderItems=" + orderItems +
				", userCouponIds='" + userCouponIds + '\'' +
				", actualPay=" + actualPay +
				'}';
	}
}