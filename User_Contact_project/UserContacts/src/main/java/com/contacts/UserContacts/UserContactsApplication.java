package com.contacts.UserContacts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserContactsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserContactsApplication.class, args);
	}

}
