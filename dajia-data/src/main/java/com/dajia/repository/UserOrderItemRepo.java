package com.dajia.repository;

import com.dajia.domain.UserOrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserOrderItemRepo extends CrudRepository<UserOrderItem, Long> {
	List<UserOrderItem> findByProductItemIdAndIsActive(Long productItemId, String isActive);

	List<UserOrderItem> findByProductItemIdAndUserIdAndAndIsActiveOrderByOrderItemId(Long productItemId,
                                                                                     Long userId, String isActive);

	List<UserOrderItem> findByTrackingIdAndIsActive(String trackingId, String isActive);
}