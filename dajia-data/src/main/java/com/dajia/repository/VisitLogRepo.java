package com.dajia.repository;

import com.dajia.domain.VisitLog;
import org.springframework.data.repository.CrudRepository;

public interface VisitLogRepo extends CrudRepository<VisitLog, Long> {
}