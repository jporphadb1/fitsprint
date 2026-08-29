package com.jporpha.fitsprint.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fitsprint.jwt")
public class JwtProperties {

    /** Chave HMAC em Base64, ver application.yml (fitsprint.jwt.secret). */
    private String secret;

    private int expirationDays = 7;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(int expirationDays) {
        this.expirationDays = expirationDays;
    }
}
