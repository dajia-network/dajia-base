package com.dajia.repository;

import com.dajia.domain.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepo extends CrudRepository<Location, Long> {

	List<Location> findByLocationTypeOrderByLocationKey(String locationType);

	List<Location> findByParentKeyOrderByLocationKey(Long parentKey);

	Location findByLocationKey(Long locationKey);
}