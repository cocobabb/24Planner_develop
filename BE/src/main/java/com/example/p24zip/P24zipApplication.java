package com.example.p24zip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class P24zipApplication {

	public static void main(String[] args) {
		SpringApplication.run(P24zipApplication.class, args);
	}

}
