package com.home.config;

import com.home.utils.EncryptDecryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
@Profile("!default")
public class SSLConfig {
    private final String keyStore;
    private final String keyStorePassword;
    private final String truststore;
    private final String truststorePassword;
    private final EncryptDecryptUtil encryptDecryptUtil;
    public SSLConfig( @Value("${ssl.keystore}") String keyStore,
                      @Value("${ssl.keystore.password}") String keyStorePassword,
                      @Value("${ssl.truststore}") String truststore,
                      @Value("${ssl.truststore.password}") String truststorePassword,
                     EncryptDecryptUtil encryptDecryptUtil) {
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.truststore = truststore;
        this.truststorePassword = truststorePassword;
        this.encryptDecryptUtil = encryptDecryptUtil;
    }

    // This method will be called after the bean is initialized

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> sslCustomizer() {
        return factory -> {
            log.info("Configuring SSL with keystore: {} and truststore: {}", keyStore, truststore);

            Ssl ssl = new Ssl();
            ssl.setKeyStore(keyStore);
            try {
                ssl.setKeyStorePassword(encryptDecryptUtil.decrypt(keyStorePassword));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ssl.setTrustStore(truststore);
            try {
                ssl.setTrustStorePassword(encryptDecryptUtil.decrypt(truststorePassword));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ssl.setKeyStoreType("PKCS12");
            ssl.setTrustStoreType("PKCS12");
            factory.setSsl(ssl);
        };
    }

}
