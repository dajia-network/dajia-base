package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.ProductTag;

public interface ProductTagRepo extends CrudRepository<ProductTag, Long> {

	List<ProductTag> findByIsActive(String isActive);
}