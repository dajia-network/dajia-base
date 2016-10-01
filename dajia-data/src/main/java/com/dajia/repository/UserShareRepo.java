package com.dajia.repository;

import com.dajia.domain.UserShare;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserShareRepo extends CrudRepository<UserShare, Long> {

	List<UserShare> findByUserIdAndVisitUserIdAndProductItemIdAndShareType(Long userId, Long visitUserId,
                                                                           Long productItemId, Integer shareType);

	List<UserShare> findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(Long orderId,
                                                                                Long productItemId, Integer shareType);

	List<UserShare> findByShareType(Integer shareType);
}