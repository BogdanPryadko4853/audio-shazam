package com.audio.gatewayservice.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Маршруты для API
                .route("metadata-api", r -> r.path("/api/v1/tracks/**")
                        .uri("lb://metadata-service"))
                .route("ingestion-api", r -> r.path("/api/v1/audio/**")
                        .uri("lb://ingestion-service"))
                .route("fingerprint-api", r -> r.path("/api/v1/fingerprints/**")
                        .uri("lb://fingerprint-service"))
                .route("metadata-docs", r -> r.path("/metadata-service/v3/api-docs/**")
                        .uri("lb://metadata-service"))
                .route("ingestion-docs", r -> r.path("/ingestion-service/v3/api-docs/**")
                        .uri("lb://ingestion-service"))
                .route("fingerprint-docs", r -> r.path("/fingerprint-service/v3/api-docs/**")
                        .uri("lb://fingerprint-service"))
                .route("swagger-ui", r -> r.path("/swagger-ui.html", "/webjars/**")
                        .uri("lb://gateway-service"))
                .build();
    }
}