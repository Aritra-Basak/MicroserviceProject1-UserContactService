package com.contacts.UserContacts.Service;

import java.util.List;

import com.contacts.UserContacts.entity.Contacts;
import com.contacts.UserContacts.entity.ServiceResponse;

public interface ContactService {
	
	public ServiceResponse getContactDetails(long userId);
	public List<Contacts> contactForUser(long userId);
	public ServiceResponse createNewContact(Contacts contactModel,long userId);
	public ServiceResponse deleteContactByUserId(long userId);
	public ServiceResponse deletContactByCiD(long cId);
	public ServiceResponse editContactByCiD(Contacts contactModel,long cId);


}
