package com.dajia.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dajia.domain.ProductItem;
import com.dajia.domain.User;
import com.dajia.domain.UserCoupon;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserOrderItem;
import com.dajia.domain.UserReward;
import com.dajia.domain.UserShare;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.UserContactRepo;
import com.dajia.repository.UserOrderItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRefundRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.repository.UserShareRepo;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.util.DajiaResult;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.OrderFilterVO;
import com.dajia.vo.OrderVO;
import com.dajia.vo.ProductVO;
import com.dajia.vo.ProgressVO;

@Service
public class OrderService {
	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserOrderItemRepo orderItemRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserContactRepo userContactRepo;

	@Autowired
	private UserRefundRepo refundRepo;

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private UserShareRepo userShareRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserCouponService userCouponService;

	@Transactional
	public UserOrder generateRobotOrder(Long productId, Integer quantity) {
		ProductVO product = productService.loadProductDetail(productId);
		UserOrder order = new UserOrder();
		order.orderStatus = OrderStatus.PENDING_PAY.getKey();
		order.orderDate = new Date();
		order.quantity = quantity;
		order.unitPrice = product.currentPrice;
		order.totalPrice = product.currentPrice.multiply(new BigDecimal(quantity));
		order.productId = product.productId;
		order.productItemId = product.productItemId;

		order.userId = 0L;
		order.payType = 0;
		order.contactName = "";
		order.contactMobile = "";
		order.address = "";
		orderRepo.save(order);
		productService.productSold(order);
		return order;
	}

	public OrderVO convertOrderVO(UserOrder order) {
		OrderVO ov = new OrderVO();
		ov.orderId = order.orderId;
		ov.userId = order.userId;
		ov.trackingId = order.trackingId;
		ov.productId = order.productId;
		ov.productItemId = order.productItemId;
		ov.productDesc = order.productDesc;
		ov.productShared = order.productShared;
		ov.quantity = order.quantity;
		ov.payType = order.payType;
		ov.orderDate = order.orderDate;
		ov.unitPrice = order.unitPrice;
		ov.totalPrice = order.totalPrice;
		ov.actualPay = order.actualPay;
		ov.postFee = order.postFee;
		ov.logisticAgent = order.logisticAgent;
		ov.logisticTrackingId = order.logisticTrackingId;
		ov.contactName = order.contactName;
		ov.contactMobile = order.contactMobile;
		ov.address = order.address;
		ov.comments = order.comments;
		ov.userComments = order.userComments;
		ov.adminComments = order.adminComments;
		ov.orderStatus = order.orderStatus;
		ov.orderStatus4Show = CommonUtils.getOrderStatusStr(order.orderStatus);
		ov.logisticAgent4Show = CommonUtils.getLogisticAgentStr(order.logisticAgent);
		ov.payType4Show = CommonUtils.getPayTypeStr(order.payType);
		return ov;
	}

	public Page<UserOrder> loadOrdersByUserIdByPage(Long userId, List<Integer> orderStatusList, Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage_5);
		Page<UserOrder> orders = orderRepo.findByUserIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(userId,
				orderStatusList, CommonUtils.ActiveStatus.YES.toString(), pageable);
		return orders;
	}

	public List<UserOrder> loadOrdersByUserId(Long userId, List<Integer> orderStatusList) {
		List<UserOrder> orders = orderRepo.findByUserIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(userId,
				orderStatusList, CommonUtils.ActiveStatus.YES.toString());
		return orders;
	}

	public Page<UserOrder> loadOrdersByPage(Integer pageNum, OrderFilterVO orderFilter) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<UserOrder> orders = null;
		String orderType = "all";
		Integer orderStatus = -1;
		String trackingId = null;
		Long productId = null;
		if (null != orderFilter) {
			orderType = orderFilter.type;
			orderStatus = orderFilter.status;
			trackingId = orderFilter.trackingId;
			productId = orderFilter.productId;
		}
		// 如果搜索订单跟踪号直接返回结果
		if (null != trackingId) {
			orders = orderRepo.findByTrackingIdAndIsActiveOrderByOrderDateDesc(trackingId, ActiveStatus.YES.toString(),
					pageable);
			return orders;
		}

		List<Integer> orderStatusList = new ArrayList<Integer>();
		if (orderStatus >= 0) {
			orderStatusList.add(orderStatus);
		} else {
			orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.CLOSED.getKey());
			orderStatusList.add(CommonUtils.OrderStatus.CANCELLED.getKey());
		}

		// 根据产品ID返回结果
		if (null != productId) {
			orders = orderRepo.findOrdersByProductId(productId, orderStatusList, ActiveStatus.YES.toString(), pageable);
			return orders;
		}

		// exclude pending payment orders
		if (orderType.equals("all")) {
			orders = orderRepo.findByOrderStatusInAndIsActiveOrderByOrderDateDesc(orderStatusList,
					ActiveStatus.YES.toString(), pageable);
		} else if (orderType.equals("real")) {
			orders = orderRepo.findByOrderStatusInAndUserIdNotAndIsActiveOrderByOrderDateDesc(orderStatusList, 0L,
					ActiveStatus.YES.toString(), pageable);
		}
		return orders;
	}

	public void fillOrderVO(OrderVO ov, UserOrder order) {
		User user = userRepo.findByUserId(order.userId);
		if (null != user) {
			ov.userName = user.userName;
			ov.userHeadImgUrl = user.headImgUrl;
		}
		if (null != ov.productItemId) {
			ov.productVO = productService.loadProductDetailByItemId(ov.productItemId);
			if (null != ov.productVO) {
				ov.totalProductPrice = ov.unitPrice.multiply(new BigDecimal(ov.quantity));
				ov.rewardValue = rewardService.calculateRewards(ov.orderId, ov.productVO);
				ov.refundValue = calculateRefundValue(order);
			}
		} else {
			ov.orderItems = order.orderItems;
			ov.totalProductPrice = new BigDecimal(0);
			ov.rewardValue = new BigDecimal(0);
			ov.refundValue = new BigDecimal(0);
			for (UserOrderItem oi : ov.orderItems) {
				ov.totalProductPrice = ov.totalProductPrice.add(oi.unitPrice.multiply(new BigDecimal(oi.quantity)));
				oi.productVO = productService.loadProductDetailByItemId(oi.productItemId);
				ov.rewardValue = ov.rewardValue.add(rewardService.calculateRewards(ov.orderId, oi.productVO));
			}
			ov.refundValue = calculateRefundValue(order);
		}
	}

	/**
	 * 计算一个订单的退款
	 *
	 * @param order
	 * @return
	 */
	private BigDecimal calculateRefundValue(UserOrder order) {
		BigDecimal refundVal = new BigDecimal(0);

		if (null != order.productItemId) {
			// single order
			ProductVO pv = productService.loadProductDetailByItemId(order.productItemId);
			refundVal = order.unitPrice.add(pv.currentPrice.negate()).multiply(new BigDecimal(order.quantity));

			/** 计算Promote返利 **/
			if (pv.isPromoted.equalsIgnoreCase(CommonUtils.Y)) {
				List<UserShare> userShares = userShareRepo.findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(
						order.orderId, pv.productItemId, CommonUtils.ShareType.BUY_SHARE.getKey());

				if (false == CollectionUtils.isEmpty(userShares)) {
					refundVal = refundVal.add(new BigDecimal(userShares.size()));
				}
			}

		} else {
			// cart order
			for (UserOrderItem oi : order.orderItems) {
				ProductVO pv = productService.loadProductDetailByItemId(oi.productItemId);
				refundVal = refundVal.add(oi.unitPrice.add(pv.currentPrice.negate()).multiply(
						new BigDecimal(oi.quantity)));

				/** 计算Promote返利 **/
				if (pv.isPromoted.equalsIgnoreCase(CommonUtils.YesNoStatus.YES.toString())) {
					List<UserShare> userShares = userShareRepo
							.findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(order.orderId,
									pv.productItemId, CommonUtils.ShareType.BUY_SHARE.getKey());

					if (false == CollectionUtils.isEmpty(userShares)) {
						refundVal = refundVal.add(new BigDecimal(userShares.size()));
					}
				}
			}
		}

		/** 只能退不超过当前订单总价的金额 **/
		// BigDecimal productItemTotalPrice = orderUnitPrice.multiply(new
		// BigDecimal(orderQuantity));
		if (null == order.actualPay) {
			order.actualPay = order.totalPrice;
		}
		if (refundVal.compareTo(order.actualPay) > 0) {
			refundVal = order.actualPay;
		}

		return refundVal;
	}

	public void testCalcRefundValue(Long orderId) {
		UserOrder userOrder = orderRepo.findOne(orderId);
		BigDecimal refundValue = calculateRefundValue(userOrder);
		logger.info("result: " + refundValue);
	}

	public void orderRefund(ProductItem productItem) {
		Long productItemId = productItem.productItemId;

		// refund single product order
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		// 所有买过这个商品的订单
		List<UserOrder> orderList = orderRepo.findByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(
				productItem.productItemId, orderStatusList, CommonUtils.ActiveStatus.YES.toString());

		if (null != orderList) {

			for (UserOrder userOrder : orderList) {

				String trackingId = userOrder.trackingId;

				if (null != userOrder.paymentId) {
					BigDecimal refundValue = calculateRefundValue(userOrder);

					if (refundValue.compareTo(BigDecimal.ZERO) <= 0) {
						logger.warn("orderRefund, refund value <= 0, productItemId={}, trackingId={}", productItemId,
								trackingId);
						continue;
					}

					try {
						refundService.createRefund(userOrder, refundValue, CommonUtils.RefundType.REFUND.getKey());
						apiService.applyRefund(userOrder.paymentId, refundValue, CommonUtils.refund_type_refund);
						logger.info("orderRefund, userOrder, success, productItemId={}, trackingId={}, value="
								+ refundValue.doubleValue(), productItemId, trackingId);
					} catch (Exception e) {
						logger.error("orderRefund, userOrder, error, productItemId={}, trackingId=" + trackingId,
								productItemId, e);
					}
				}
			}
		}
		// refund cart order
		List<UserOrderItem> orderItemList = orderItemRepo.findByProductItemIdAndIsActive(productItem.productItemId,
				CommonUtils.ActiveStatus.YES.toString());

		if (null != orderItemList) {

			for (UserOrderItem userOrderItem : orderItemList) {
				String trackingId = userOrderItem.userOrder.trackingId;
				// 检查是否该订单下所有产品均已打价结束，否则不处理

				boolean isAllProductItemsExpiredForThisOrder = true;
				List<ProductItem> productItems = productService.loadProductItemsByTrackingId(trackingId);
				for (ProductItem pi : productItems) {
					if (pi.productStatus.intValue() != CommonUtils.ProductStatus.EXPIRED.getKey().intValue()) {
						logger.warn(
								"orderRefund for productItem {} is not created because there is productItem no expired, trackingId {}",
								pi.productItemId, trackingId);
						isAllProductItemsExpiredForThisOrder = false;
						break;
					}
				}

				if (false == isAllProductItemsExpiredForThisOrder) {
					continue;
				}

				if (userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.PAIED.getKey()
						|| userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.DELEVERING.getKey()
						|| userOrderItem.userOrder.orderStatus == CommonUtils.OrderStatus.DELEVRIED.getKey()) {

					BigDecimal refundValue = calculateRefundValue(userOrderItem.userOrder);

					if (refundValue.compareTo(BigDecimal.ZERO) <= 0) {
						logger.warn("orderRefund, cart order, refund value <= 0, trackingId={}", trackingId);
						continue;
					}

					try {
						refundService.createRefund(userOrderItem.userOrder, refundValue,
								CommonUtils.RefundType.REFUND.getKey());
						apiService.applyRefund(userOrderItem.userOrder.paymentId, refundValue,
								CommonUtils.refund_type_refund);
						logger.info(
								"orderRefund, cart order, success, trackingId={}, value=" + refundValue.doubleValue(),
								trackingId);

					} catch (Exception e) {
						logger.error("orderRefund, cart order, error, trackingId={}", trackingId, e);
					}
				}
			}
		}
	}

	public OrderVO getOrderDetailByTrackingId(String trackingId) {
		UserOrder order = orderRepo.findByTrackingId(trackingId);
		if (null == order) {
			return null;
		}
		OrderVO ov = this.convertOrderVO(order);
		this.fillOrderVO(ov, order);
		if (null != order.userCouponIds && order.userCouponIds.length() > 0) {
			DajiaResult res = userCouponService.findCouponsByOrderId(order.orderId, order.userId);
			ov.appliedCouponsObj = (List<UserCoupon>) res.data;
		}
		return ov;
	}

	public OrderVO getOrderDetailByTrackingId4Progress(String trackingId, Long orderItemId) {
		OrderVO ov = this.getOrderDetailByTrackingId(trackingId);
		if (orderItemId != 0) {
			UserOrderItem oi = orderItemRepo.findOne(orderItemId);
			ov.productId = oi.productId;
			ov.productItemId = oi.productItemId;
			ov.productShared = oi.productShared;
			ov.unitPrice = oi.unitPrice;
			ov.quantity = oi.quantity;
			ov.productVO = productService.loadProductDetailByItemId(oi.productItemId);
		}
		Map<Long, LoginUserVO> rewardSrcUserMap = rewardService.getRewardSrcUsers(ov.orderId, ov.productItemId);
		ov.rewardSrcUsers = rewardSrcUserMap.values();
		ProductItem productItem = productItemRepo.findOne(ov.productItemId);
		List<ProgressVO> progressList = new ArrayList<ProgressVO>();
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		List<UserOrder> orderList = orderRepo.findTop5ByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderIdDesc(
				ov.productItemId, orderStatusList, CommonUtils.ActiveStatus.YES.toString());
		BigDecimal comparePrice = productItem.currentPrice;
		for (UserOrder userOrder : orderList) {
			ProgressVO pv = new ProgressVO();
			pv.progressType = CommonUtils.refund_type_refund;
			pv.orderId = ov.orderId;
			pv.productId = ov.productId;
			pv.productItemId = ov.productItemId;
			pv.orderDate = userOrder.orderDate;
			pv.orderQuantity = userOrder.quantity;
			pv.priceOff = userOrder.unitPrice.add(comparePrice.negate());
			comparePrice = userOrder.unitPrice;
			progressList.add(pv);
		}
		List<UserReward> rewardList = rewardRepo
				.findTop5ByRefOrderIdAndProductItemIdAndRewardStatusOrderByCreatedDateDesc(ov.orderId,
						ov.productItemId, CommonUtils.RewardStatus.PENDING.getKey());
		for (UserReward userReward : rewardList) {
			ProgressVO pv = new ProgressVO();
			pv.progressType = CommonUtils.refund_type_reward;
			pv.orderId = ov.orderId;
			pv.productItemId = ov.productItemId;
			pv.orderDate = userReward.createdDate;
			pv.orderUserName = rewardSrcUserMap.get(userReward.orderUserId).userName;
			progressList.add(pv);
		}
		Collections.sort(progressList, Collections.reverseOrder());
		ov.progressList = progressList;

		List<UserShare> userShares = userShareRepo.findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(
				ov.orderId, ov.productItemId, CommonUtils.ShareType.BUY_SHARE.getKey());
		ov.userShares = userShares;

		return ov;
	}

	public String generateOrderInfoStr(UserOrder order) {
		StringBuilder sb = new StringBuilder();
		sb.append("oid:");
		sb.append(order.trackingId);
		sb.append("|uid:");
		sb.append(order.userId);
		sb.append("|ptid:");
		sb.append(order.productItemId);
		sb.append("|cname:");
		sb.append(order.contactName);
		sb.append("|cmobile:");
		sb.append(order.contactMobile);
		return sb.toString();
	}

	public UserOrder findOneOrderByProductItemIdAndUserId(Long productItemId, Long userId) {
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		List<UserOrder> orderList = orderRepo.findByProductItemIdAndUserIdAndOrderStatusInAndIsActiveOrderByOrderId(
				productItemId, userId, orderStatusList, CommonUtils.ActiveStatus.YES.toString());
		if (null != orderList) {
			for (UserOrder userOrder : orderList) {
				return userOrder;
			}
		}
		return null;
	}

	public UserOrderItem findOneOrderItemByProductItemIdAndUserId(Long productItemId, Long userId) {
		List<UserOrderItem> orderItemList = orderItemRepo.findByProductItemIdAndUserIdAndAndIsActiveOrderByOrderItemId(
				productItemId, userId, CommonUtils.ActiveStatus.YES.toString());
		if (null != orderItemList) {
			for (UserOrderItem orderItem : orderItemList) {
				return orderItem;
			}
		}
		return null;
	}

	public boolean orderValidate(UserOrder order) {
		if (null != order.productId) {
			if (order.totalPrice.compareTo(order.unitPrice.multiply(new BigDecimal(order.quantity)).add(order.postFee)) < 0) {
				logger.error("Total price validation failed");
				logger.error("Page total: " + order.totalPrice.toPlainString() + "; Calculated total: "
						+ order.unitPrice.multiply(new BigDecimal(order.quantity)).add(order.postFee).toPlainString());
				order.totalPrice = order.unitPrice.multiply(new BigDecimal(order.quantity)).add(order.postFee);
			}
			ProductItem item = productItemRepo.findOne(order.productItemId);
			if (null == item) {
				logger.error("Product item validation failed");
				return false;
			}
			if (order.unitPrice.compareTo(item.currentPrice) < 0) {
				logger.error("Product price validation failed");
				return false;
			}
			return true;
		} else if (null != order.orderItems) {
			BigDecimal productAmount = new BigDecimal(0);
			for (UserOrderItem oi : order.orderItems) {
				ProductItem item = productItemRepo.findOne(oi.productItemId);
				if (null == item) {
					logger.error("Product item validation failed");
					return false;
				}
				if (oi.unitPrice.compareTo(item.currentPrice) < 0) {
					logger.error("Product price validation failed");
					return false;
				}
				productAmount = productAmount.add(oi.unitPrice.multiply(new BigDecimal(oi.quantity)));
			}
			if (order.totalPrice.compareTo(productAmount.add(order.postFee)) < 0) {
				logger.error("Total price validation failed");
				logger.error("Page total: " + order.totalPrice.toPlainString() + "; Calculated total: "
						+ productAmount.add(order.postFee).toPlainString());
				order.totalPrice = productAmount.add(order.postFee);
			}
			return true;
		} else {
			return false;
		}
	}

	@Deprecated
	public void orderRefundCheck() {
		List<ProductItem> productItems = productService.loadAllExpiredProducts();
		for (ProductItem productItem : productItems) {
			this.orderRefund(productItem);
		}
	}

	public List<UserOrder> getOrderListBySales(Long userId, Date startDate, Date endDate) {
		List<UserReward> rewardList = rewardRepo.findByRefUserIdAndRewardDateBetweenAndRewardStatus(userId, startDate,
				endDate, CommonUtils.RewardStatus.SALES.getKey());
		List<UserOrder> orderList = new ArrayList<UserOrder>();
		Set<Long> orderIdSet = new HashSet<Long>();
		for (UserReward userReward : rewardList) {
			if (!orderIdSet.contains(userReward.orderId)) {
				orderIdSet.add(userReward.orderId);
				UserOrder order = orderRepo.findOne(userReward.orderId);
				orderList.add(order);
			}
		}
		return orderList;
	}

	public OrderVO getRefOrderDetail(Long productId, Long refOrderId) {
		UserOrder order = orderRepo.findOne(refOrderId);
		OrderVO ov = this.convertOrderVO(order);
		this.fillOrderVO(ov, order);

		Long productItemId = null;
		if (null != ov.productItemId) {
			productItemId = ov.productItemId;
			ov.orderItemId = 0L;
		} else if (null != ov.orderItems) {
			for (UserOrderItem oi : ov.orderItems) {
				if (oi.productId.longValue() == productId.longValue()) {
					productItemId = oi.productItemId;
					ov.orderItemId = oi.orderItemId;
					break;
				}
			}
		} else {
			return null;
		}

		List<UserShare> userShares = userShareRepo.findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(
				ov.orderId, productItemId, CommonUtils.ShareType.BUY_SHARE.getKey());
		ov.userShares = userShares;

		return ov;
	}

}
