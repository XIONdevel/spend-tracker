package com.noix.spendtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootApplication
@EnableJpaRepositories
public class SpendTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpendTrackerApplication.class, args);
	}

}
