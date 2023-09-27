/**
 * 
 */
package com.user.user_service.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aritra
 *
 */
@Document(collection = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@Indexed(unique = true) //makes the below parameter unique, if an existing id is used to save a new entry it will not get saved.
	private Long userId;
	private String name;
	private String phone;
	private String password;
	private int contactCount;
	List<Contacts> contacts = new ArrayList<>();
	
	

	public User(Long userId, String name, String phone,String password) {
		this.userId = userId;
		this.name = name;
		this.phone = phone;
		this.password=password;
	}
}
