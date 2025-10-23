package com.ssafy.clonenova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class CloneNovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloneNovaApplication.class, args);

	}

}
