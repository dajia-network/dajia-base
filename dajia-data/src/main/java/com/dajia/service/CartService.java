package com.dajia.service;

import com.dajia.domain.UserCart;
import com.dajia.repository.UserCartRepo;
import com.dajia.vo.CartItemVO;
import com.dajia.vo.ProductVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
	Logger logger = LoggerFactory.getLogger(CartService.class);

	@Autowired
	private UserCartRepo cartRepo;

	@Autowired
	private ProductService productService;

	public void add2Cart(UserCart cart) {
		UserCart uc = cartRepo.findByUserIdAndProductId(cart.userId, cart.productId);
		if (null == uc) {
			cartRepo.save(cart);
		} else {
			uc.quantity = uc.quantity + 1;
			cartRepo.save(uc);
		}
	}

	public void removeFromCart(Long userId, Long productId) {
		UserCart cart = cartRepo.findByUserIdAndProductId(userId, productId);
		if (null != cart) {
			cartRepo.delete(cart);
		}
	}

	public void editCart(Long userId, Long cartId, Integer quantity) {
		UserCart cart = cartRepo.findOne(cartId);
		if (null != cart && cart.userId.equals(userId)) {
			cart.quantity = quantity;
			cartRepo.save(cart);
		}
	}

	public List<CartItemVO> getCartByUserId(Long userId) {
		List<CartItemVO> cart = new ArrayList<CartItemVO>();
		List<UserCart> cartItems = cartRepo.findByUserIdOrderByCreatedDateDesc(userId);
		for (UserCart cartItem : cartItems) {
			ProductVO pv = productService.loadProductDetail(cartItem.productId);
			CartItemVO cartItemVO = new CartItemVO();
			cartItemVO.cartId = cartItem.cartId;
			cartItemVO.quantity = cartItem.quantity;
			cartItemVO.productId = pv.productId;
			cartItemVO.name = pv.name;
			cartItemVO.shortName = pv.shortName;
			cartItemVO.productItemId = pv.productItemId;
			cartItemVO.currentPrice = pv.currentPrice;
			cartItemVO.postFee = pv.postFee;
			cartItemVO.stock = pv.stock;
			cartItemVO.productStatus = pv.productStatus;
			cartItemVO.buyQuota = pv.buyQuota;
			cartItemVO.imgUrl4List = pv.imgUrl4List;
			cart.add(cartItemVO);
		}
		return cart;
	}
}
