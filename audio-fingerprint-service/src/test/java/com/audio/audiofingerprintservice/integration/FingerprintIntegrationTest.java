package com.audio.audiofingerprintservice.integration;

import com.audio.audiofingerprintservice.AudioFingerprintServiceApplication;
import com.audio.audiofingerprintservice.dto.FingerprintMatchResponse;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AudioFingerprintServiceApplication.class
)
@Testcontainers
class FingerprintIntegrationTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:1.4.0"))
            .withServices(S3);

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // S3 (MinIO compatible)
        registry.add("minio.url", () -> localstack.getEndpointOverride(S3).toString());
        registry.add("minio.access-key", () -> localstack.getAccessKey());
        registry.add("minio.secret-key", () -> localstack.getSecretKey());
        registry.add("minio.bucket", () -> "test-bucket");
    }

    @BeforeAll
    static void setup() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(localstack.getEndpointOverride(S3).toString())
                .credentials(localstack.getAccessKey(), localstack.getSecretKey())
                .build();

        if (!minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket("test-bucket")
                .build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket("test-bucket")
                    .build());
        }
    }

    @Test
    void fullIntegrationTest() {
        // 1. Prepare test file
        byte[] audioData = "test audio content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "audio", "test.mp3", "audio/mpeg", audioData);

        // 2. Prepare request
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new org.springframework.core.io.ByteArrayResource(audioData) {
            @Override
            public String getFilename() {
                return "test.mp3";
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 3. Send request
        ResponseEntity<FingerprintMatchResponse> response = restTemplate.exchange(
                "/api/v1/fingerprints/search",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                FingerprintMatchResponse.class
        );

        // 4. Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}