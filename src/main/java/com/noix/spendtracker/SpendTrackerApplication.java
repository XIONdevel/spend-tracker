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

//	public static void main(String[] args) throws Exception {
//		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//		keyGen.init(256); // 256-bit key
//		SecretKey secretKey = keyGen.generateKey();
//		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//		System.out.println("Your AES-256 Key (Base64 Encoded): " + encodedKey);
//	}

}
