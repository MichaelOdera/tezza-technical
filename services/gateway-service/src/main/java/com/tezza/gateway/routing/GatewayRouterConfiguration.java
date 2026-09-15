package com.tezza.gateway.routing;

import com.tezza.gateway.config.GatewayRouteProperties;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouterConfiguration {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/products/**", operation = @Operation(summary = "Route product requests")),
            @RouterOperation(path = "/api/customers/**", operation = @Operation(summary = "Route customer requests")),
            @RouterOperation(path = "/api/loans/**", operation = @Operation(summary = "Route loan requests")),
            @RouterOperation(path = "/api/notifications/**", operation = @Operation(summary = "Route notification requests"))
    })
    public RouterFunction<ServerResponse> gatewayRoutes(GatewayRouteProperties properties, GatewayRequestHandler handler) {
        RouterFunctions.Builder builder = RouterFunctions.route();

        // Loop through the custom routes defined under your new 'tezza.gateway' application prefix
        for (GatewayRouteProperties.Route route : properties.getRoutes()) {
            builder.route(
                    RequestPredicates.path(route.getPath()),
                    request -> handler.forward(request, route)
            );
        }

        return builder.build();
    }
}