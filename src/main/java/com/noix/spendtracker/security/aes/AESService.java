package com.noix.spendtracker.security.aes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AESService {

    private static final Logger logger = LoggerFactory.getLogger(AESService.class);
    private final SecretKey aesKey;

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";

    public static final Integer IV_LENGTH_ENCRYPT = 12;
    public static final Integer TAG_LENGTH_ENCRYPT = 16;


    @Autowired
    public AESService(@Value("${spring.security.aes.key}") String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);

        if (decodedKey.length != 32) {
            throw new IllegalArgumentException("AES key must 32 bytes. Length: " + decodedKey.length + " bytes.");
        }
        aesKey = new SecretKeySpec(decodedKey, AES_ALGORITHM);
    }

    public String encrypt(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Passed text can not be null");
        }

        try {
            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv); //fill with random bytes

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                 | BadPaddingException | IllegalBlockSizeException e) {
            logger.error("Probably something wrong with constants or Cipher creation");
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("Check GCMParameterSpec object creation and usage");
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipherText) throws AEADBadTagException {
        if (cipherText == null) {
            throw new IllegalArgumentException("Passed cipherText can not be null");
        }

        try {
            byte[] decodedCipher = Base64.getDecoder().decode(cipherText);

            //extract iv
            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipher, 0, iv, 0, iv.length);

            //extract text itself
            byte[] encrypted = new byte[decodedCipher.length - IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipher, IV_LENGTH_ENCRYPT, encrypted, 0, encrypted.length);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            logger.error("Probably something wrong with constants or Cipher creation");
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("Check GCMParameterSpec object creation and usage");
            throw new RuntimeException(e);
        }
    }
}