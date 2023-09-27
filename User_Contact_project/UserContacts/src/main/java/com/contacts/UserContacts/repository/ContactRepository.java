package com.contacts.UserContacts.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contacts.UserContacts.entity.Contacts;

@Repository
public interface ContactRepository extends MongoRepository<Contacts, Long> {

	List<Contacts> findByUserId(Long userId);
	void deleteByUserId(Long userId);
	Contacts findBycId(long cId);
}
