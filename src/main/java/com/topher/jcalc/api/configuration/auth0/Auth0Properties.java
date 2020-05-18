package com.topher.jcalc.api.configuration.auth0;

public class Auth0Properties {

    private String clientId;
    private String clientSecret;
    private String issuer;
    private String audience;
    private long expirationTime;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(final String audience) {
        this.audience = audience;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(final long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
