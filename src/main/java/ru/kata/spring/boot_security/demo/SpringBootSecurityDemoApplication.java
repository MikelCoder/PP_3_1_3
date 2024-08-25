package ru.kata.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {
	static UserServiceImpl userService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "password123";
		String encodedPassword = encoder.encode(rawPassword);
		System.out.println(encodedPassword);
	}
}
