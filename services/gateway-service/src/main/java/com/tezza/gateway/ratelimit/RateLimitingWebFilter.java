package com.tezza.gateway.ratelimit;

import com.tezza.gateway.config.RateLimitProperties;
import com.tezza.gateway.events.GatewayEventsFacade;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingWebFilter implements WebFilter {
    private static final RedisScript<Long> SCRIPT = RedisScript.of(
            "local count = redis.call('INCR', KEYS[1]) " +
                    "if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end " +
                    "return count",
            Long.class);

    private final ReactiveStringRedisTemplate redis;
    private final RateLimitProperties properties;
    private final GatewayEventsFacade events;

    public RateLimitingWebFilter(ReactiveStringRedisTemplate redis,
                                 RateLimitProperties properties,
                                 GatewayEventsFacade events) {
        this.redis = redis;
        this.properties = properties;
        this.events = events;
    }

    @Override
    @NullMarked
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (!properties.getRedis().isEnabled()) {
            return chain.filter(exchange);
        }

        String client = client(exchange);
        String key = properties.getRedis().getKeyPrefix() + client;

        return redis.execute(SCRIPT, List.of(key), String.valueOf(properties.getRedis().getWindowSeconds()))
                .next()
                .flatMap(count -> handleCount(count, client, exchange, chain))
                .onErrorResume(error -> serviceUnavailable(exchange));
    }

    private Mono<Void> handleCount(Long count,
                                    String client,
                                    ServerWebExchange exchange,
                                    WebFilterChain chain) {
        if (count > properties.getRequestsPerMinute()) {
            events.rateLimitExceeded(client, exchange.getRequest().getPath().value());
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().set(
                    HttpHeaders.RETRY_AFTER,
                    String.valueOf(properties.getRedis().getWindowSeconds()));
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private Mono<Void> serviceUnavailable(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return exchange.getResponse().setComplete();
    }

    private String client(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",", 2)[0].trim();
        }
        if (exchange.getRequest().getRemoteAddress() == null) {
            return "unknown";
        }
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}