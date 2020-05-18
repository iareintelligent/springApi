package com.topher.jcalc.api.model.auth0;

public class Jwt {
    private final String accessToken;
    private final int expiresIn;
    private final long expiresAt;

    public Jwt(final String accessToken, final int expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.expiresAt = System.currentTimeMillis() + (expiresIn * 1000);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
