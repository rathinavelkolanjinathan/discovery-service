package com.home.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerateApp {
   /* public static void main(String[] args) throws Exception {
        EncryptDecryptUtil util = new EncryptDecryptUtil();
        String secretKey = util.getSecretKeyToString();
        System.out.println("Generated Secret Key: " + secretKey);
    }*/
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Use 128, 192, or 256 bits based on your requirement
        SecretKey secretKey = keyGen.generateKey();
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key: " + base64Key);
    }

}
