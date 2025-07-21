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
                        .uri("lb://AUDIO-METADATA-SERVICE"))
                .route("ingestion-api", r -> r.path("/api/v1/audio/**")
                        .uri("lb://AUDIO-INGESTION-SERVICE"))
                .route("fingerprint-api", r -> r.path("/api/v1/fingerprints/**")
                        .uri("lb://AUDIO-FINGERPRINT-SERVICE"))
                .route("metadata-docs", r -> r.path("/metadata-service/v3/api-docs/**")
                        .uri("lb://AUDIO-METADATA-SERVICE"))
                .route("ingestion-docs", r -> r.path("/ingestion-service/v3/api-docs/**")
                        .uri("lb://AUDIO-INGESTION-SERVICE"))
                .route("fingerprint-docs", r -> r.path("/fingerprint-service/v3/api-docs/**")
                        .uri("lb://AUDIO-FINGERPRINT-SERVICE"))
                .route("swagger-ui", r -> r.path("/swagger-ui.html", "/webjars/**")
                        .uri("lb://AUDIO-GATEWAY"))
                .build();
    }
}