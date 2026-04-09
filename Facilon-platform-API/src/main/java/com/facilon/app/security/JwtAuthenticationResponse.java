package com.facilon.app.security;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private boolean mustChangePassword;

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
        this.mustChangePassword = false;
    }

    public JwtAuthenticationResponse(String accessToken, boolean mustChangePassword) {
        this.accessToken = accessToken;
        this.mustChangePassword = mustChangePassword;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
