package com.noix.spendtracker.security.aes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.AEADBadTagException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AESServiceTest {

    private final String plainText = "Definitely not wierd text for testing purpose";
    private static AESService service;
    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha256.digest("Some hard to remember phrase".getBytes(StandardCharsets.UTF_8));
        String key = Base64.getEncoder().encodeToString(keyBytes);
        service = new AESService(key);
    }

    @Test
    @DisplayName("Should encrypt and decrypt string successfully")
    void should_encrypt_decrypt_success() throws AEADBadTagException {
        final String encrypted = service.encrypt(plainText);
        assertNotEquals(plainText, encrypted);
        final String decrypted = service.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }
}