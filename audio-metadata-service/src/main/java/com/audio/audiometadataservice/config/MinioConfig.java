package com.audio.audiometadataservice.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnMissingBean(type = "io.minio.MinioClient")
@Profile("!test")
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    
        @Bean
        public MinioClient minioClient(MinioProperties properties) {
            return MinioClient.builder()
                    .endpoint(properties.getInternalUrl())
                    .credentials(properties.getAccessKey(), properties.getSecretKey())
                    .build();

    }
}