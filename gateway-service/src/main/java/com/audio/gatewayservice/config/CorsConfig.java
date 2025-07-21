package com.audio.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        return new CorsWebFilter(this::getCorsConfiguration);
    }

    private CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        return config;
    }
}