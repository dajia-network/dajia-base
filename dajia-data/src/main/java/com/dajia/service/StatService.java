package com.dajia.service;

import com.dajia.domain.UserOrder;
import com.dajia.domain.UserReward;
import com.dajia.domain.UserShare;
import com.dajia.repository.*;
import com.dajia.util.CommonUtils;
import com.dajia.vo.OrderVO;
import com.dajia.vo.PaginationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StatService {
	Logger logger = LoggerFactory.getLogger(StatService.class);

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserOrderItemRepo orderItemRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private UserRefundRepo refundRepo;

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private UserShareRepo userShareRepo;

	@Autowired
	private OrderService orderService;

	public PaginationVO<OrderVO> getRewardStatsByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		List<UserReward> rewards = rewardRepo.findByRewardStatusAndIsActive(
				CommonUtils.RewardStatus.COMPLETED.getKey(), CommonUtils.ActiveStatus.YES.toString());
		List<UserShare> shares = userShareRepo.findByShareType(CommonUtils.ShareType.BUY_SHARE.getKey());
		Set<Long> orderIds = new HashSet<Long>();
		for (UserReward reward : rewards) {
			orderIds.add(reward.orderId);
		}
		for (UserShare share : shares) {
			orderIds.add(share.orderId);
		}
		Page<UserOrder> orders = orderRepo.findByOrderIdInAndIsActiveOrderByOrderDateDesc(orderIds,
				CommonUtils.ActiveStatus.YES.toString(), pageable);
		List<OrderVO> orderVoList = new ArrayList<OrderVO>();
		for (UserOrder order : orders) {
			OrderVO ov = orderService.convertOrderVO(order);
			List<UserShare> userShares = new ArrayList<UserShare>();
			for (UserShare us : shares) {
				if (ov.orderId.equals(us.orderId)) {
					userShares.add(us);
				}
			}
			ov.userShares = userShares;
			orderService.fillOrderVO(ov, order);
			orderVoList.add(ov);
		}
		PaginationVO<OrderVO> page = CommonUtils.generatePaginationVO(orders, pageNum);
		page.results = orderVoList;
		return page;
	}
}
