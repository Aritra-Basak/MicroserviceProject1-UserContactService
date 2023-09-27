package com.user.user_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.user.user_service.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long>{
	//In repository try to give the name of the methods declared here same as the name of the variables in the entity/model
	User findByUserId(Long userId);
	List<User> findByName(String name);
	
	List<User> findByPhone(String phone);
	

}
