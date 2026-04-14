package com.home.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptDecryptUtil {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;
    // 16 bytes IV for AES
    @Value("${cipher.secretKey}")
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
        if (keyAsString == null || keyAsString.isEmpty()) {
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
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecret(),spec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        byte[] encryptedBytesIv = new byte[IV_SIZE + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedBytesIv, 0, IV_SIZE);
        System.arraycopy(encryptedBytes, 0, encryptedBytesIv, IV_SIZE, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }

    public String decrypt(String cipherText) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(cipherText);
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy( encryptedWithIv, 0,iv,0, IV_SIZE);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecret(),spec);
        byte[] encryptedBytes = new byte[ encryptedWithIv.length-IV_SIZE];
        System.arraycopy(encryptedWithIv, IV_SIZE,encryptedBytes,0, encryptedBytes.length);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

}
