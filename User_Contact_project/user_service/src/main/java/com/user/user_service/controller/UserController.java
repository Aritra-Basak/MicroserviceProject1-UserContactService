/**
 * 
 */
package com.user.user_service.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.user_service.entity.AuthenticationRequest;
import com.user.user_service.entity.ServiceResponse;
import com.user.user_service.entity.User;
import com.user.user_service.service.JwtTokenFunctions;
import com.user.user_service.service.UserService;

/**
 * @author Aritra
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;	
	
	@Autowired
	JwtTokenFunctions jwt;
	
	@GetMapping("/getUsers")
	public ServiceResponse getContacts(HttpServletRequest request){
		ServiceResponse response = new ServiceResponse();
		String authorizationHeader = request.getHeader("Authorization");
		long userId=jwt.verifyUser(authorizationHeader);
		if(userId==0) {
			response.setResMessage("Check Your UserName and Password or maybe there is no user with that credentials");
			response.setStatus(HttpStatus.BAD_REQUEST);
		}else {
			response=userService.getUser(userId);
		}
		return response;
	}
	@PostMapping("/newUser")
	public ServiceResponse newUser(@RequestBody User userModel){
		return userService.newUser(userModel);
	}
	@DeleteMapping("/deleteUser")
	public ServiceResponse deleteUSer(HttpServletRequest request){
		ServiceResponse response = new ServiceResponse();
		String authorizationHeader = request.getHeader("Authorization");
		long userId=jwt.verifyUser(authorizationHeader);
		if(userId==0 && userId==9999999) {
			response.setResMessage("Check Your UserName and Password or maybe there is no user with that credentials");
			response.setStatus(HttpStatus.BAD_REQUEST);
		}else {
			response=userService.deleteUser(userId);
		}
		return response;
	}
	@PutMapping("/editUser")
	public ServiceResponse editUser(HttpServletRequest request,@RequestBody User userModel){
		ServiceResponse response = new ServiceResponse();
		String authorizationHeader = request.getHeader("Authorization");
		long userId=jwt.verifyUser(authorizationHeader);
		if(userId==0 && userId==9999999) {
			response.setResMessage("Check Your UserName and Password or maybe there is no user with that credentials");
			response.setStatus(HttpStatus.BAD_REQUEST);
		}else {
			response=userService.editUser(userId, userModel);
		}
		return response;
		
	}
	@PostMapping("/getPermissionToken")
	public ServiceResponse getPermissionToken(@RequestBody AuthenticationRequest auth, HttpServletRequest request) {
		return jwt.getAuthToken(auth, request);	
	}

}
