package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.dajia.domain.UserContact;
import com.dajia.domain.UserOrderItem;
import com.dajia.domain.UserRefund;
import com.dajia.domain.UserShare;

public class OrderVO {

	public Long orderId;

	public Long orderItemId;

	public String trackingId;

	public Integer quantity;

	public Integer orderStatus;

	public Integer payType;

	public Long productId;

	public Long productItemId;

	public String productDesc;

	public String productShared;

	public Long refUserId;

	public Long refOrderId;

	public Long userId;

	public String userName;

	public String userHeadImgUrl;

	public BigDecimal unitPrice;

	public BigDecimal totalPrice;

	public BigDecimal totalProductPrice;

	public BigDecimal postFee;

	public BigDecimal rewardValue;

	public BigDecimal refundValue;

	public String logisticAgent;

	public String logisticTrackingId;

	public String contactName;

	public String contactMobile;

	public String address;

	public String comments;

	public String userComments;

	public String adminComments;

	public String payType4Show;

	public String orderStatus4Show;

	public String logisticAgent4Show;

	public Date orderDate;

	public UserContact userContact;

	public ProductVO productVO;

	public Collection<LoginUserVO> rewardSrcUsers;

	public List<ProgressVO> progressList;

	public List<UserRefund> refundList;

	public List<ProductVO> productVOList;

	public List<CartItemVO> cartItems;

	public List<UserOrderItem> orderItems;

	public List<UserShare> userShares;
}
