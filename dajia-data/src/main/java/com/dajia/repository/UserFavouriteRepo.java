package com.dajia.repository;

import com.dajia.domain.UserFavourite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserFavouriteRepo extends CrudRepository<UserFavourite, Long> {

	List<UserFavourite> findByUserIdOrderByCreatedDateDesc(Long userId);

	UserFavourite findByUserIdAndProductId(Long userId, Long productId);
}