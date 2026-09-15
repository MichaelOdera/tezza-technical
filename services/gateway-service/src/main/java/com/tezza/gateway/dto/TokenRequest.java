package com.tezza.gateway.dto;

import java.util.Map;

public class TokenRequest {
    private String subject;
    private Map<String, Object> claims;

    // Getters and Setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Map<String, Object> getClaims() { return claims; }
    public void setClaims(Map<String, Object> claims) { this.claims = claims; }
}