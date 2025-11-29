package com.home.config;


import com.home.utils.EncryptDecryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
@Profile("!default")
public class SSLConfig {
    private  final String keyStorePath;
    private final String keyStorePassword;
    private  final String truststore;
    private final String truststorePassword;

    private final EncryptDecryptUtil encryptDecryptUtil;

    public SSLConfig(String keyStorePath,
                     String keyStorePassword,
                     String truststore,
                     String truststorePassword,
                     EncryptDecryptUtil encryptDecryptUtil) {
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.truststore = truststore;
        this.truststorePassword = truststorePassword;
        this.encryptDecryptUtil = encryptDecryptUtil;
    }
}
