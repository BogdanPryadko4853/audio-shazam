package com.audio.audiofingerprintservice;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public MinioClient minioClient() {
        return mock(MinioClient.class);
    }

    @Bean
    @Primary
    public ElasticsearchClient elasticsearchClient() {
        return mock(ElasticsearchClient.class);
    }

}