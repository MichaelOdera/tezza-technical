package com.tezza.gateway.routing;

import com.tezza.gateway.config.GatewayRouteProperties;
import com.tezza.gateway.events.GatewayEventsFacade;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

@Component
public class GatewayRequestHandler {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            HttpHeaders.HOST.toLowerCase(),
            HttpHeaders.CONTENT_LENGTH.toLowerCase(),
            HttpHeaders.TRANSFER_ENCODING.toLowerCase(),
            HttpHeaders.CONNECTION.toLowerCase(),
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            "upgrade"
    );

    private final WebClient webClient;
    // Replace with your actual facade package if different
    private final GatewayEventsFacade events;

    public GatewayRequestHandler(WebClient webClient, GatewayEventsFacade events) {
        this.webClient = webClient;
        this.events = events;
    }

    public Mono<ServerResponse> forward(ServerRequest request, GatewayRouteProperties.Route route) {
        // Safe URI construction that handles paths and query parameters perfectly
        String querySuffix = request.uri().getRawQuery() != null ? "?" + request.uri().getRawQuery() : "";
        URI target = URI.create(route.getUri() + request.path() + querySuffix);

        return request.bodyToMono(byte[].class)
                .defaultIfEmpty(new byte[0])
                .flatMap(body -> webClient.method(request.method())
                        .uri(target)
                        .headers(headers -> copyHeaders(request.headers().asHttpHeaders(), headers))
                        .bodyValue(body)
                        .exchangeToMono(response -> toServerResponse(response, request, route)));
    }

    private Mono<ServerResponse> toServerResponse(
            ClientResponse response,
            ServerRequest request,
            GatewayRouteProperties.Route route) {

        // Extract status code as a primitive integer or HttpStatusCode matching Spring Boot 4 specifications
        HttpStatusCode statusCode = response.statusCode();
        events.routeForwarded(route.getId(), request.path(), statusCode.value());

        ServerResponse.BodyBuilder builder = ServerResponse.status(statusCode);
        builder.headers(headers -> copyHeaders(response.headers().asHttpHeaders(), headers));

        return builder.body(response.bodyToMono(byte[].class), byte[].class);
    }

    private void copyHeaders(HttpHeaders source, HttpHeaders target) {
        source.forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                target.put(name, values);
            }
        });
    }
}