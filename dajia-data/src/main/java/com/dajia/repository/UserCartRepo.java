package com.dajia.repository;

import com.dajia.domain.UserCart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserCartRepo extends CrudRepository<UserCart, Long> {

	List<UserCart> findByUserIdOrderByCreatedDateDesc(Long userId);

	UserCart findByUserIdAndProductId(Long userId, Long productId);
}