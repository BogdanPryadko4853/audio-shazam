package com.audio.audioingestionservice.config;

import com.audio.audioingestionservice.controller.MetadataServiceClient;
import com.audio.audioingestionservice.model.AudioUploadEvent;
import com.audio.audioingestionservice.service.AudioStorageService;
import io.minio.MinioClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, AudioUploadEvent> kafkaTemplate() {
        return mock(KafkaTemplate.class);
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
    public ConcurrentKafkaListenerContainerFactory<String, AudioUploadEvent> kafkaListenerContainerFactory() {
        return mock(ConcurrentKafkaListenerContainerFactory.class);
    }

    @Bean
    @Primary
    public RecordMessageConverter messageConverter() {
        return mock(StringJsonMessageConverter.class);
    }

    @Bean
    @Primary
    public KafkaTransactionManager<String, AudioUploadEvent> kafkaTransactionManager() {
        return mock(KafkaTransactionManager.class);
    }

    @Bean
    @Primary
    public AdminClient kafkaAdminClient() {
        return mock(AdminClient.class);
    }

    @Bean
    @Primary
    public MinioClient minioClient() {
        return mock(MinioClient.class);
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

    @Bean
    @Primary
    public MetadataServiceClient metadataServiceClient() {
        return mock(MetadataServiceClient.class);
    }
}