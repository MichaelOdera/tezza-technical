package com.tezza.gateway.filter;

import com.tezza.gateway.config.GatewaySecurityProperties;
import com.tezza.gateway.events.GatewayEventsFacade;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec; // Native Java class for AES keys
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationWebFilter implements WebFilter {

    private final GatewaySecurityProperties properties;
    private final SecretKey signingKey;
    private final SecretKey encryptionKey; 
    private final GatewayEventsFacade events;

    public JwtAuthenticationWebFilter(GatewaySecurityProperties properties, GatewayEventsFacade events) {
        this.properties = properties;
        this.events = events;

        if (properties.getJwtSecret() == null || properties.getJwtSecret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("tezza.gateway.security.jwt-secret must be at least 32 bytes");
        }

        byte[] secretBytes = properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        
        // FIX: Replaced Keys.aesKeyFor with standard SecretKeySpec
        this.encryptionKey = new SecretKeySpec(secretBytes, "AES"); 
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (properties.getPublicPaths().stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            events.authenticationRejected(path, "missing_bearer_token");
            return unauthorized(exchange);
        }

        try {
            var claims = Jwts.parser()
                    .decryptWith(encryptionKey) 
                    .verifyWith(signingKey)     
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseEncryptedClaims(authorization.substring(7).trim()) 
                    .getPayload();

            ServerWebExchange authenticatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-Authenticated-Subject", claims.getSubject())
                            .build())
                    .build();

            return chain.filter(authenticatedExchange);

        } catch (JwtException | IllegalArgumentException exception) {
            events.authenticationRejected(path, "invalid_token");
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
