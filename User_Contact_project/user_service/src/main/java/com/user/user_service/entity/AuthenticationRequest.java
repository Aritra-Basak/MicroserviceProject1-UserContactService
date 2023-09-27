package com.user.user_service.entity;

import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.Data;

@Data
@FieldDefaults(level=AccessLevel.PRIVATE)
public class AuthenticationRequest {
	
	 String userName;
	 String password;
	
}
