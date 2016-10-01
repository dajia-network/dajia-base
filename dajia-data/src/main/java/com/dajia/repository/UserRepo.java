package com.dajia.repository;

import com.dajia.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

	User findByUserId(Long userId);

	User findByMobile(String mobile);

	User findByOauthUserIdAndOauthType(String oauthUserId, String oauthType);

	Page<User> findByIsActiveOrderByCreatedDateDesc(String isActive, Pageable pageable);

	Page<User> findByIsSalesAndIsActiveOrderByCreatedDateDesc(String isSales, String isActive, Pageable pageable);

	Page<User> findByUserNameContainingAndIsActiveOrderByCreatedDateDesc(String userName, String isActive,
                                                                         Pageable pageable);

	List<User> findByRefUserIdAndCreatedDateBetweenAndIsActive(Long refUserId, Date startDate, Date endDate,
                                                               String isActive);
}