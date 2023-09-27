/**
 * 
 */
package com.contacts.UserContacts.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aritra
 *
 */
@Document(collection = "contacts")
public class Contacts {

	@Id
	@Indexed(unique = true)
	@JsonProperty("cId")
	private Long cId;
	private String name;
	private String contactNumber;
	private Long userId;
	
	
	public Contacts(Long cId, String name, String contactNumber, Long userId) {
		this.cId = cId;
		this.name = name;
		this.contactNumber = contactNumber;
		this.userId = userId;
	}


	public Long getcId() {
		return cId;
	}


	public void setcId(Long cId) {
		this.cId = cId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getContactNumber() {
		return contactNumber;
	}


	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
	
}
