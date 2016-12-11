package com.dajia.repository;

import com.dajia.domain.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface UserOrderRepo extends CrudRepository<UserOrder, Long> {

	UserOrder findByTrackingId(String trackingId);

	UserOrder findByPaymentId(String paymentId);

	UserOrder findByOrderIdAndOrderStatusAndIsActive(Long orderId, Integer orderStatus, String isActive);

	UserOrder findByUserIdAndProductItemIdAndOrderStatusAndIsActive(Long userId, Long productItemId,
			Integer orderStatus, String isActive);

	Page<UserOrder> findByUserIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(Long userId,
			List<Integer> orderStatusList, String isActive, Pageable pageable);

	List<UserOrder> findByUserIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(Long userId,
			List<Integer> orderStatusList, String isActive);

	List<UserOrder> findByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderDateDesc(Long productItemId,
			List<Integer> orderStatusList, String isActive);

	List<UserOrder> findByProductItemIdAndUserIdAndOrderStatusInAndIsActiveOrderByOrderId(Long productItemId,
			Long userId, List<Integer> orderStatusList, String isActive);

	List<UserOrder> findTop5ByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderIdDesc(Long productItemId,
			List<Integer> orderStatusList, String isActive);

	Page<UserOrder> findByIsActiveOrderByOrderDateDesc(String isActive, Pageable pageable);

	Page<UserOrder> findByUserIdNotAndIsActiveOrderByOrderDateDesc(Long userId, String isActive, Pageable pageable);

	Page<UserOrder> findByOrderStatusInAndIsActiveOrderByOrderDateDesc(List<Integer> orderStatusList, String isActive,
			Pageable pageable);

	Page<UserOrder> findByOrderStatusInAndUserIdNotAndIsActiveOrderByOrderDateDesc(List<Integer> orderStatusList,
			Long userId, String isActive, Pageable pageable);

	Page<UserOrder> findByOrderIdInAndIsActiveOrderByOrderDateDesc(Set<Long> orderIds, String isActive,
			Pageable pageable);

	Page<UserOrder> findByTrackingIdAndIsActiveOrderByOrderDateDesc(String trackingId, String isActive,
			Pageable pageable);

	UserOrder findByOrderIdAndIsActive(Long orderId, String s);
}