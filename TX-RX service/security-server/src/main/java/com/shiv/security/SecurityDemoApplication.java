package com.shiv.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SecurityDemoApplication {
	public static void main(String[] args) {
		String uuid="609ddfbd-5a5d-4503-b515-05c0f4e7936b__";
		SpringApplication.run(SecurityDemoApplication.class, args);
	}
}
