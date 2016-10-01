package com.dajia.repository;

import com.dajia.domain.Product;
import com.dajia.domain.ProductItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ProductItemRepo extends CrudRepository<ProductItem, Long> {

	List<ProductItem> findByProductStatusAndIsActiveOrderByExpiredDateAsc(Integer productStatus, String isActive);

	Page<ProductItem> findByProductStatusAndIsActiveOrderByExpiredDateAsc(Integer productStatus,
																		  String isActive, Pageable pageable);

	Page<ProductItem> findByIsActiveOrderByStartDateDesc(String isActive, Pageable pageable);

	Page<ProductItem> findByProductInAndIsActiveOrderByStartDateDesc(List<Product> products, String isActive,
																	 Pageable pageable);

	Page<ProductItem> findByProductStatusInAndStartDateBeforeAndIsActiveOrderByProductStatusAscFixTopDescExpiredDateAsc(
			List<Integer> productStatusList, Date startDate, String isActive, Pageable pageable);
}