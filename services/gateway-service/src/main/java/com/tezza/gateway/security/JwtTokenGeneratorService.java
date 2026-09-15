package com.tezza.gateway.security;

import com.tezza.gateway.config.GatewaySecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec; // Native Java class for AES keys
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenGeneratorService {

    private final GatewaySecurityProperties properties;
    private final SecretKey signingKey;
    private final SecretKey encryptionKey;

    public JwtTokenGeneratorService(GatewaySecurityProperties properties) {
        this.properties = properties;
        
        byte[] secretBytes = properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("JWT Secret must be at least 32 bytes");
        }
        
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        
        // FIX: Replaced Keys.aesKeyFor with standard SecretKeySpec
        this.encryptionKey = new SecretKeySpec(secretBytes, "AES"); 
    }

    public String generateEncryptedToken(String subject, Map<String, Object> additionalClaims, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .issuer(properties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .claims(additionalClaims)
                .signWith(signingKey) 
                .encryptWith(encryptionKey, Jwts.ENC.A256GCM) 
                .compact();
    }
}
