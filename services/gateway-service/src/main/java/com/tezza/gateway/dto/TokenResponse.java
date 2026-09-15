package com.tezza.gateway.dto;

public class TokenResponse {
    private final String token;
    private final String tokenType = "Bearer";

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
}