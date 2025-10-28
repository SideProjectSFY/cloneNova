package com.ssafy.clonenova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ssafy.clonenova.config.PortOneProperties;

@EnableConfigurationProperties(PortOneProperties.class)
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class CloneNovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloneNovaApplication.class, args);

	}

}