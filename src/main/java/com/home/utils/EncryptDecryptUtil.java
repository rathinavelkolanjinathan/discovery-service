package com.home.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class EncryptDecryptUtil {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    @Value("$cipher.secretKey")
    private String SECRET_KEY_STR;
    private SecretKey SECRET_KEY;

    // Add your encryption and decryption methods here
    @PostConstruct
    public void init() {
        try {
            SECRET_KEY = stringToSecretKey(SECRET_KEY_STR);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private SecretKey stringToSecretKey(String keyAsString) throws NoSuchAlgorithmException {
        if (keyAsString == null) {
            keyAsString = getSecretKeyToString();
        }
        byte[] decodeKey = Base64.getDecoder().decode(keyAsString);
        return new SecretKeySpec(decodeKey, 0, decodeKey.length, ALGORITHM);
    }

    public String getSecretKeyToString() throws NoSuchAlgorithmException {
        byte[] encodeKey = getSecret().getEncoded();
        return Base64.getEncoder().encodeToString(encodeKey);
    }

    public void setSecretKey(String keyAsString) {
        this.SECRET_KEY_STR = keyAsString;
        init();
    }

    private SecretKey getSecret() throws NoSuchAlgorithmException {
        SECRET_KEY = SECRET_KEY == null ?
                generateSecretKey() : SECRET_KEY;
        return SECRET_KEY;
    }

    private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecret());
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecret());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes);
    }

}
