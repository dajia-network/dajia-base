package com.dajia.repository;

import com.dajia.domain.Property;
import org.springframework.data.repository.CrudRepository;

public interface PropertyRepo extends CrudRepository<Property, Long> {

	Property findByPropertyKey(String propertyKey);
}