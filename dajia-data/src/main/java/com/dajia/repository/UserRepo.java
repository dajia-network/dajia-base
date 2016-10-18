package com.dajia.repository;

import com.dajia.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedQuery;
import java.util.Date;
import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

	User findByUserId(Long userId);

	User findByMobile(String mobile);

	User findByUserNameAndIsActive(String userName, String isActive);

	User findByUserNameAndPasswordAndIsActiveAndIsAdmin(String userName, String password, String isActive, String isAdmin);

	User findByOauthUserIdAndOauthType(String oauthUserId, String oauthType);

	Page<User> findByIsActiveOrderByCreatedDateDesc(String isActive, Pageable pageable);

	Page<User> findByIsSalesAndIsActiveOrderByCreatedDateDesc(String isSales, String isActive, Pageable pageable);

	Page<User> findByUserNameContainingAndIsActiveOrderByCreatedDateDesc(String userName, String isActive,
                                                                         Pageable pageable);

	List<User> findByRefUserIdAndCreatedDateBetweenAndIsActive(Long refUserId, Date startDate, Date endDate,
                                                               String isActive);
	@Modifying
	@Transactional
	@Query("update User u set u.password=:newPass where u.userId=:userId and u.password=:oldPass")
	int updateUserPass(@Param("userId") Long userId, @Param("oldPass") String oldPass, @Param("newPass") String newPass);
}