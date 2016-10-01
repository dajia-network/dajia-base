package com.dajia.service;

import com.dajia.domain.VisitLog;
import com.dajia.repository.VisitLogRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitLogService {
	Logger logger = LoggerFactory.getLogger(VisitLogService.class);

	@Autowired
	private VisitLogRepo visitLogRepo;

	public void addVisitLog(VisitLog visitLog, Integer logType, String visitIp) {
		visitLog.logType = logType;
		visitLog.visitIp = visitIp;
		visitLogRepo.save(visitLog);
	}
}
