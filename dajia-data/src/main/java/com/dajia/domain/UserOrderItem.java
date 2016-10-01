package com.dajia.domain;

import com.dajia.vo.ProductVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@JsonIgnoreProperties(value = { "userOrder" })
@Table(name = "user_order_item")
public class UserOrderItem extends BaseModel {

	@Column(name = "order_item_id")
	@Id
	@GeneratedValue
	public Long orderItemId;

	@Column(name = "tracking_id")
	public String trackingId;

	@Column(name = "product_id", nullable = false)
	public Long productId;

	@Column(name = "product_item_id", nullable = false)
	public Long productItemId;

	@Column(name = "user_id", nullable = false)
	public Long userId;

	@Column(name = "product_shared")
	public String productShared;

	@Column(name = "quantity")
	public Integer quantity;

	@Column(name = "unit_price")
	public BigDecimal unitPrice;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	public UserOrder userOrder;

	@Transient
	public ProductVO productVO;

	@Override
	public String toString() {
		return "UserOrderItem{" +
				"orderItemId=" + orderItemId +
				", trackingId='" + trackingId + '\'' +
				", productId=" + productId +
				", productItemId=" + productItemId +
				", userId=" + userId +
				", productShared='" + productShared + '\'' +
				", quantity=" + quantity +
				", unitPrice=" + unitPrice +
				", orderId=" + userOrder.orderId +
				'}';
	}
}