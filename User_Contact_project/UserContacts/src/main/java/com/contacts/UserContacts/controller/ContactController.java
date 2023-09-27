/**
 * 
 */
package com.contacts.UserContacts.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contacts.UserContacts.Service.ContactService;
import com.contacts.UserContacts.Service.JwtTokenFunctionsContacts;
import com.contacts.UserContacts.entity.Contacts;
import com.contacts.UserContacts.entity.ServiceResponse;

/**
 * @author Aritra
 *
 */
@RestController
@RequestMapping("/contacts")
public class ContactController {
	
	@Autowired
	ContactService cs;
	
	@Autowired
	JwtTokenFunctionsContacts jwt;
	
	@GetMapping("/getMyContacts")
	public ServiceResponse getMyContacts(@RequestParam (required = true, defaultValue = "0")long userId){
		return cs.getContactDetails(userId);
	}
	@GetMapping("/getContactsService")
	public List<Contacts> getContactsService(HttpServletRequest request){
		String authorizationHeader = request.getHeader("Authorization");
		long userId = jwt.verifyUser(authorizationHeader);
		return cs.contactForUser(userId);
	}
	@PostMapping("/newContact")
	public ServiceResponse createNewContact(@RequestBody Contacts contactModel,HttpServletRequest request){
		String authorizationHeader = request.getHeader("Authorization");
		long userId = jwt.verifyUser(authorizationHeader);
		return cs.createNewContact(contactModel,userId);
	}
	//To delete all the contacts of a user without deleting the user
	@DeleteMapping("/deleteContact")
	public ServiceResponse deleteContact(HttpServletRequest request){
		String authorizationHeader = request.getHeader("Authorization");
		long userId = jwt.verifyUser(authorizationHeader);
		return cs.deleteContactByUserId(userId);
	}
	//To delete a particular contact of a user, by the use of individual Contact ID.
	@DeleteMapping("/deleteContactByContactID")
	public ServiceResponse deleteContactByContactID(@RequestParam(required = true, defaultValue = "0")long cId){
		return cs.deletContactByCiD(cId);
	}
	
	@PutMapping("/editContact")
	public ServiceResponse editContactDetails(@RequestBody Contacts contactModel, @RequestParam(required = true, defaultValue = "0")long cId) {
		return cs.editContactByCiD(contactModel, cId);
	}

}
