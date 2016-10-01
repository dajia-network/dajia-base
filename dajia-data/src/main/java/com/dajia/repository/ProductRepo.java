package com.dajia.repository;

import com.dajia.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepo extends CrudRepository<Product, Long> {

	Product findByRefId(String refId);

	List<Product> findByProductIdInAndIsActive(List<Long> productIds, String isActive);

	List<Product> findByNameContainingAndIsActiveOrderByCreatedDateDesc(String keyword, String isActive);

}