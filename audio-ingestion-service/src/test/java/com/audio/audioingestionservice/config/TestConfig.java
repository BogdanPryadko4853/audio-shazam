package com.audio.audioingestionservice.config;

import com.audio.audioingestionservice.model.AudioUploadEvent;
import com.audio.audioingestionservice.service.AudioStorageService;
import io.minio.MinioClient;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.mockito.Mockito.mock;
@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, AudioUploadEvent> testKafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public MinioClient minioClient() {
        return mock(MinioClient.class);
    }

    @Bean
    @Primary
    public ProducerFactory<String, AudioUploadEvent> producerFactory() {
        return mock(ProducerFactory.class);
    }

    @Bean
    @Primary
    public ConsumerFactory<String, AudioUploadEvent> consumerFactory() {
        return mock(ConsumerFactory.class);
    }

    @Bean
    @Primary
    public AudioStorageService audioStorageService() {
        return mock(AudioStorageService.class);
    }

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
}