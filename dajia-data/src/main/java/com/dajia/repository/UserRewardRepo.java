package com.dajia.repository;

import com.dajia.domain.UserReward;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface UserRewardRepo extends CrudRepository<UserReward, Long> {
	List<UserReward> findByRefUserIdAndProductItemIdAndRewardStatus(Long refUserId, Long productItemId,
			Integer rewardStatus);

	List<UserReward> findByRefOrderIdAndProductItemIdAndRewardStatusIn(Long refOrderId, Long productItemId,
			List<Integer> rewardStatusList);

	List<UserReward> findTop5ByRefOrderIdAndProductItemIdAndRewardStatusOrderByCreatedDateDesc(Long refOrderId,
			Long productItemId, Integer rewardStatus);

	List<UserReward> findByOrderUserIdAndProductItemIdAndRewardStatus(Long orderUserId, Long productItemId,
			Integer rewardStatus);

	List<UserReward> findByRewardDateBeforeAndRewardStatusAndIsActive(Date rewardDate, Integer rewardStatus,
			String isActive);

	List<UserReward> findByRefUserIdAndRewardDateBetweenAndRewardStatus(Long refUserId, Date startDate, Date endDate,
			Integer rewardStatus);

	List<UserReward> findByRewardStatusAndIsActive(Integer rewardStatus, String isActive);

}