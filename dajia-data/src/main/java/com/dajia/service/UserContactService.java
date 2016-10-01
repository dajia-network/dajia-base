package com.dajia.service;

import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.repository.LocationRepo;
import com.dajia.repository.UserContactRepo;
import com.dajia.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContactService {
	Logger logger = LoggerFactory.getLogger(UserContactService.class);

	@Autowired
	private UserContactRepo userContactRepo;

	@Autowired
	private LocationRepo locationRepo;

	public UserContact updateUserContact(UserContact userContact, User user) {
		// userContact.isDefault = "Y";
		userContact.province = locationRepo.findByLocationKey(userContact.province.locationKey);
		userContact.city = locationRepo.findByLocationKey(userContact.city.locationKey);
		userContact.district = locationRepo.findByLocationKey(userContact.district.locationKey);
		if (null == userContact.contactId || 0L == userContact.contactId) {
			userContact.user = user;
			userContactRepo.save(userContact);
			markDefaultUserContact(userContact.contactId, user);
			return userContact;
		} else {
			UserContact uc = userContactRepo.findOne(userContact.contactId);
			if (null != uc && uc.user.userId == user.userId) {
				try {
					CommonUtils.copyProperties(userContact, uc);
					userContactRepo.save(uc);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return uc;
			} else {
				return null;
			}
		}
	}

	public void markDefaultUserContact(Long contactId, User user) {
		for (UserContact uc : user.getUserContacts()) {
			if (contactId.longValue() == uc.contactId.longValue()) {
				uc.isDefault = "Y";
			} else {
				uc.isDefault = "N";
			}
			userContactRepo.save(uc);
		}
	}

	public void removeUserContact(Long contactId) {
		UserContact uc = userContactRepo.findOne(contactId);
		if (null != uc) {
			uc.isActive = "N";
			userContactRepo.save(uc);
		}
	}
}
