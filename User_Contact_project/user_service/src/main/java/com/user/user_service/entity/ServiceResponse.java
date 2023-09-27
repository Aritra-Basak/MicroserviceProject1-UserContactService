/**
 * 
 */
package com.user.user_service.entity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.Data;

/**
 * @author Aritra
 *
 */
@Service
@Data
public class ServiceResponse {

	private long versionId = 1L;
	private HttpStatus status;
	private String resMessage;
	private Object resObject;
}
