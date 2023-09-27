/**
 * 
 */
package com.user.user_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aritra
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {

	@Id
	@Indexed(unique = true)
	@JsonProperty("cId")
	private Long cId;
	private String name;
	private String contactNumber;
	private Long userId;
	
	
	
}
