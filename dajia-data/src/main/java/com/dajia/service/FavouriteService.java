package com.dajia.service;

import com.dajia.domain.UserFavourite;
import com.dajia.repository.UserFavouriteRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavouriteService {
	Logger logger = LoggerFactory.getLogger(FavouriteService.class);

	@Autowired
	private UserFavouriteRepo favouriteRepo;

	public void addFavourite(UserFavourite favourite) {
		if (null == favouriteRepo.findByUserIdAndProductId(favourite.userId, favourite.productId)) {
			favouriteRepo.save(favourite);
		}
	}

	public void removeFavourite(Long userId, Long productId) {
		UserFavourite favourite = favouriteRepo.findByUserIdAndProductId(userId, productId);
		if (null != favourite) {
			favouriteRepo.delete(favourite);
		}
	}
}
