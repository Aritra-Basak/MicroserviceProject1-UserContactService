/**
 * 
 */
package com.user.user_service.service;

import java.util.List;

import com.user.user_service.entity.ServiceResponse;
import com.user.user_service.entity.User;

/**
 * @author Aritra
 * Here we will just declare all the methods that must be used in out UserService class.
 *
 */
public interface UserService {
	//Gets the info about a particular user on the basis of the id passed.
	public ServiceResponse getUser(long UserId);
	public ServiceResponse newUser(User userModel);
	public ServiceResponse deleteUser(long userId);
	public ServiceResponse editUser(long userId,User userModel);
	

}
