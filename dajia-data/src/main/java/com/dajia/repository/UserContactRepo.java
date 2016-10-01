package com.dajia.repository;

import com.dajia.domain.UserContact;
import org.springframework.data.repository.CrudRepository;

public interface UserContactRepo extends CrudRepository<UserContact, Long> {

}