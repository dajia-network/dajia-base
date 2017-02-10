package com.dajia.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserOrder;

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
	
	List<UserOrder> findTop100ByOrderStatusInAndUserIdNotAndIsActiveOrderByOrderDateDesc(List<Integer> orderStatusList,
			Long userId, String isActive);

	Page<UserOrder> findByOrderIdInAndIsActiveOrderByOrderDateDesc(Set<Long> orderIds, String isActive,
			Pageable pageable);

	Page<UserOrder> findByTrackingIdAndIsActiveOrderByOrderDateDesc(String trackingId, String isActive,
			Pageable pageable);

	UserOrder findByOrderIdAndIsActive(Long orderId, String s);

	@Query("select o from UserOrder o left join o.orderItems oi where (o.productId = ?1 or oi.productId = ?1) and o.orderStatus in (?2) and o.isActive = ?3 and o.userId != 0")
	Page<UserOrder> findOrdersByProductId(Long productId, List<Integer> orderStatusList, String isActive,
			Pageable pageable);

}