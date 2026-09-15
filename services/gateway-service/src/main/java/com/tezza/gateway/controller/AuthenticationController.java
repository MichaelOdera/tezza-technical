package com.tezza.gateway.controller;

import com.tezza.gateway.dto.TokenRequest;
import com.tezza.gateway.dto.TokenResponse;
import com.tezza.gateway.security.JwtTokenGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtTokenGeneratorService tokenGeneratorService;

    public AuthenticationController(JwtTokenGeneratorService tokenGeneratorService) {
        this.tokenGeneratorService = tokenGeneratorService;
    }

    /**
     * Endpoint to request an encrypted JWT token.
     * Accessible publicly (must be whitelisted in your public paths properties).
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request) {
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Default extra claims to an empty map if none provided
        Map<String, Object> additionalClaims = request.getClaims() != null 
                ? request.getClaims() 
                : Collections.emptyMap();

        // Token Expiration tracking set here (1 hour = 3600000 milliseconds)
        long standardExpirationTime = 3600000L; 

        String secureToken = tokenGeneratorService.generateEncryptedToken(
                request.getSubject(),
                additionalClaims,
                standardExpirationTime
        );

        return ResponseEntity.ok(new TokenResponse(secureToken));
    }
}
