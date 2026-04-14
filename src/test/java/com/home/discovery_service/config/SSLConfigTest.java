package com.home.discovery_service.config;

import com.home.config.SSLConfig;
import com.home.utils.EncryptDecryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
class SSLConfigTest {
private EncryptDecryptUtil encryptDecryptUtil;
private SSLConfig sslConfig;

@BeforeEach
    void setUp() {
            encryptDecryptUtil = mock(EncryptDecryptUtil.class);
        sslConfig = new SSLConfig(
        "keystore.p12",
        "encryptedKeystorePassword",
        "truststore.p12",
        "encryptedTruststorePassword",
        encryptDecryptUtil
        );
        }

@Test
@DisplayName("Configures SSL successfully with valid inputs")
    void configuresSslSuccessfully() throws Exception {
            when(encryptDecryptUtil.decrypt("encryptedKeystorePassword")).thenReturn("keystorePassword");
            when(encryptDecryptUtil.decrypt("encryptedTruststorePassword")).thenReturn("truststorePassword");

            ConfigurableWebServerFactory factory = mock(ConfigurableWebServerFactory.class);
        Ssl ssl = new Ssl();
        doNothing().when(factory).setSsl(any(Ssl.class));

        sslConfig.sslCustomizer().customize(factory);

        verify(factory).setSsl(argThat(argument ->
        "keystore.p12".equals(argument.getKeyStore()) &&
        "keystorePassword".equals(argument.getKeyStorePassword()) &&
        "truststore.p12".equals(argument.getTrustStore()) &&
        "truststorePassword".equals(argument.getTrustStorePassword()) &&
        "PKCS12".equals(argument.getKeyStoreType()) &&
        "PKCS12".equals(argument.getTrustStoreType())
        ));
        }

@Test
@DisplayName("Throws RuntimeException when keystore password decryption fails")
    void throwsExceptionWhenKeystorePasswordDecryptionFails() throws Exception {
            when(encryptDecryptUtil.decrypt("encryptedKeystorePassword")).thenThrow(new RuntimeException("Decryption failed"));

            ConfigurableWebServerFactory factory = mock(ConfigurableWebServerFactory.class);

        assertThrows(RuntimeException.class, () -> sslConfig.sslCustomizer().customize(factory));
        }

@Test
@DisplayName("Throws RuntimeException when truststore password decryption fails")
    void throwsExceptionWhenTruststorePasswordDecryptionFails() throws Exception {
    when(encryptDecryptUtil.decrypt("encryptedKeystorePassword")).thenReturn("keystorePassword");
    when(encryptDecryptUtil.decrypt("encryptedTruststorePassword")).thenThrow(new RuntimeException("Decryption failed"));

    ConfigurableWebServerFactory factory = mock(ConfigurableWebServerFactory.class);

    assertThrows(RuntimeException.class, () -> sslConfig.sslCustomizer().customize(factory));

}
}