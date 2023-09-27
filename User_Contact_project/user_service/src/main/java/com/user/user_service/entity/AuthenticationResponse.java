package com.user.user_service.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level=AccessLevel.PRIVATE)
public class AuthenticationResponse {
	 String token;
	 String expiredOn;
	 String tokenType;
}
