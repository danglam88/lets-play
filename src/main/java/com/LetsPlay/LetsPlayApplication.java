package com.LetsPlay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LetsPlayApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetsPlayApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			User user = new User("Dang Lam", "dang.lam@gritlab.ax", "Dang1234", "ADMIN");
			userRepository.findUserByEmail("dang.lam@gritlab.ax")
				.ifPresentOrElse(u -> System.out.println(u + " already exists"), () -> {
					System.out.println("Inserting user " + user);
					userRepository.insert(user);
				});
		};
	}

}
