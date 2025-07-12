package com.audio.audioingestionservice;

import com.audio.audioingestionservice.service.AudioStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class AudioIngestionServiceApplicationTests {

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.config.enabled", () -> false);
		registry.add("spring.cloud.bootstrap.enabled", () -> false);
		registry.add("spring.cloud.config.import-check.enabled", () -> false);
	}

	@Bean
	@Primary
	public KafkaTemplate<String, String> kafkaTemplate() {
		return mock(KafkaTemplate.class);
	}

	@Bean
	@Primary
	public ProducerFactory<String, String> producerFactory() {
		return mock(ProducerFactory.class);
	}

	@Bean
	@Primary
	public ConsumerFactory<String, String> consumerFactory() {
		return mock(ConsumerFactory.class);
	}

	@Bean
	@Primary
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		return mock(ConcurrentKafkaListenerContainerFactory.class);
	}

	@Bean
	@Primary
	public RecordMessageConverter messageConverter() {
		return new StringJsonMessageConverter();
	}

	@Bean
	@Primary
	public KafkaTransactionManager<String, String> kafkaTransactionManager() {
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
	@Primary
	public ObjectMapper objectMapper() {
		return mock(ObjectMapper.class);
	}


	@Bean
	public TestRestTemplate testRestTemplate() {
		return new TestRestTemplate();
	}


	@Test
	void contextLoads() {
	}

}
