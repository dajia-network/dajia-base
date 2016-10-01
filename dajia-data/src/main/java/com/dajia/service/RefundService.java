package com.dajia.service;

import com.dajia.domain.UserOrder;
import com.dajia.domain.UserRefund;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRefundRepo;
import com.dajia.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RefundService {
	Logger logger = LoggerFactory.getLogger(RefundService.class);

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserRefundRepo refundRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductRepo productRepo;

	public void createRefund(UserOrder order, BigDecimal refundValue, Integer refundType) {
		UserRefund refund = new UserRefund();
		refund.productId = order.productId;
		refund.productItemId = order.productItemId;
		refund.orderId = order.orderId;
		refund.userId = order.userId;
		refund.refundValue = refundValue;
		refund.refundType = refundType;
		refund.refundStatus = CommonUtils.RefundStatus.PENDING.getKey();
		refundRepo.save(refund);
	}

	public void createRefundByWebhook(String chargeId, BigDecimal refundValue, Integer refundType) {
		UserOrder order = orderRepo.findByPaymentId(chargeId);
		UserRefund refund = new UserRefund();
		refund.productId = order.productId;
		refund.productItemId = order.productItemId;
		refund.orderId = order.orderId;
		refund.userId = order.userId;
		refund.refundValue = refundValue;
		refund.refundType = refundType;
		refund.refundDate = new Date();
		refund.refundStatus = CommonUtils.RefundStatus.COMPLETE.getKey();
		refundRepo.save(refund);
	}

	public void updateRefund(String chargeId, Integer refundStatus) {
		updateRefund(chargeId, refundStatus, null);
	}

	public void updateRefund(String chargeId, Integer refundStatus, String msg) {

		UserOrder order = orderRepo.findByPaymentId(chargeId);
		if (null == order) {
			logger.error("update Refund failed because findByPaymentId has no result by chargeId: {} at {}", chargeId,
					System.currentTimeMillis());
			return;
		}
		List<UserRefund> refunds = refundRepo.findByOrderIdAndRefundTypeAndIsActive(order.orderId,
				CommonUtils.RefundType.REFUND.getKey(), CommonUtils.ActiveStatus.YES.toString());
		// 一个订单只应该有一个普通退款
		if (refunds.size() != 1) {
			logger.error(
					"update Refund failed because findByOrderIdAndRefundTypeAndIsActive size is {} other than 1 at {}",
					refunds.size(), System.currentTimeMillis());
			return;
		}
		UserRefund refund = refunds.get(0);
		refund.refundDate = new Date();
		refund.refundStatus = refundStatus;
		refundRepo.save(refund);
		logger.info("update Refund success for order {} at {}", order.orderId, System.currentTimeMillis());
	}

	public void retryRefund(String jobToken) {
		logger.info("retryRefund job {} starts at {}", jobToken, System.currentTimeMillis());
		List<UserRefund> refundList = refundRepo.findByRefundStatusAndIsActive(
				CommonUtils.RefundStatus.FAILED.getKey(), CommonUtils.ActiveStatus.YES.toString());
		if (null == refundList || refundList.size() == 0) {
			logger.info("No failed refund data found.");
		} else {
			for (UserRefund refund : refundList) {
				logger.info("Start retry failed refund {}", refund.refundId);
				UserOrder order = orderRepo.findOne(refund.orderId);
				if (null == order) {
					logger.error("retryRefund job {}, refund {} failed because no order found at {}", jobToken,
							refund.refundId, System.currentTimeMillis());
					continue;
				}
				try {
					this.updateRefund(order.paymentId, CommonUtils.RefundStatus.RETRYING.getKey());
					apiService.applyRefund(order.paymentId, refund.refundValue, CommonUtils.refund_type_refund);
					logger.info(
							"orderRefund, userOrder, success, trackingId={}, value=" + refund.refundValue.doubleValue(),
							order.trackingId);
				} catch (Exception e) {
					logger.error("orderRefund, userOrder, error, trackingId={}", order.trackingId, e);
				}
			}
		}
	}

	public List<UserRefund> getRefundListByOrderId(Long orderId) {
		List<Integer> refundStatusList = new ArrayList<Integer>();
		refundStatusList.add(CommonUtils.RefundStatus.COMPLETE.getKey());
		refundStatusList.add(CommonUtils.RefundStatus.FAILED.getKey());
		refundStatusList.add(CommonUtils.RefundStatus.PENDING.getKey());
		refundStatusList.add(CommonUtils.RefundStatus.RETRYING.getKey());
		List<UserRefund> refundList = refundRepo.findByOrderIdAndRefundStatusInAndIsActive(orderId, refundStatusList,
				CommonUtils.ActiveStatus.YES.toString());
		for (UserRefund userRefund : refundList) {
			userRefund.refundType4Show = CommonUtils.getRefundTypeStr(userRefund.refundType);
			userRefund.refundStatus4Show = CommonUtils.getRefundStatusStr(userRefund.refundStatus);
		}
		return refundList;
	}
}
