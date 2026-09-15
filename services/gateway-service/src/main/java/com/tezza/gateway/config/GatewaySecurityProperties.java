package com.tezza.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "tezza.gateway.security")
public class GatewaySecurityProperties {
    private String jwtSecret;
    private String issuer;
    private List<String> publicPaths = new ArrayList<String>();
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) { this.issuer = issuer; }
    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) { this.publicPaths = publicPaths; }
}