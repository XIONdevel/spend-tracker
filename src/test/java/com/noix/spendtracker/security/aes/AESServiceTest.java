package com.noix.spendtracker.security.aes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AESServiceTest {

    static final String plainText = "Definitely not wierd text for testing purpose";
    static AESService service;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha256.digest("Some hard to remember phrase".getBytes(StandardCharsets.UTF_8));
        String key = Base64.getEncoder().encodeToString(keyBytes);
        service = new AESService(key);
    }

    @Test
    @DisplayName("Should encrypt and decrypt string successfully")
    void encrypt_decrypt_success() {
        final String encrypted = service.encrypt(plainText);
        assertNotEquals(plainText, encrypted);
        final String decrypted = service.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }
}