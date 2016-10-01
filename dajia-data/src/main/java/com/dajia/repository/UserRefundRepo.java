package com.dajia.repository;

import com.dajia.domain.UserRefund;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRefundRepo extends CrudRepository<UserRefund, Long> {
	List<UserRefund> findByOrderIdAndRefundTypeAndIsActive(Long orderId, Integer refundType, String isActive);

	List<UserRefund> findByOrderIdAndRefundStatusInAndIsActive(Long orderId, List<Integer> refundStatusList,
                                                               String isActive);

	List<UserRefund> findByRefundStatusAndIsActive(Integer refundStatus, String isaActive);
}