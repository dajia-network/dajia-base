package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Product;

public interface ProductRepo extends CrudRepository<Product, Long> {

	Product findByRefId(String refId);

	List<Product> findByProductIdInAndIsActive(List<Long> productIds, String isActive);

	List<Product> findByNameContainingAndIsActiveOrderByCreatedDateDesc(String keyword, String isActive);

	List<Product> findByTags_TagIdAndIsActiveOrderByCreatedDateDesc(Long tagId, String isActive);

}