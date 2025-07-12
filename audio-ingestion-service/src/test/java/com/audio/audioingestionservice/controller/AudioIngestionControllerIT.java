package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.dto.AudioUploadResponse;
import com.audio.audioingestionservice.dto.TrackRequest;
import com.audio.audioingestionservice.model.TrackMetadata;
import com.audio.audioingestionservice.service.AudioEventProducer;
import com.audio.audioingestionservice.service.AudioStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class AudioIngestionControllerIT {

    @LocalServerPort
    private int port;

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.bootstrap.enabled", () -> false);
        registry.add("spring.cloud.config.import-check.enabled", () -> false);
        registry.add("minio.endpoint", () -> "http://localhost:9000");
        registry.add("minio.access-key", () -> "test-access-key");
        registry.add("minio.secret-key", () -> "test-secret-key");
        registry.add("minio.bucket-name", () -> "test-bucket");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private AudioStorageService storageService;

    @MockBean
    private AudioEventProducer eventProducer;

    @MockBean
    private MetadataServiceClient metadataClient;

    @Test
    void shouldUploadAudioWithMetadata() throws Exception {
        // Arrange
        when(storageService.uploadAudio(any())).thenReturn("sources/test-key.mp3");

        TrackMetadata mockMetadata = TrackMetadata.builder()
                .id("track-123")
                .title("Test Song")
                .artist("Artist")
                .duration(180)
                .audioUrl("sources/test-key.mp3")
                .build();

        when(metadataClient.createTrack(any(TrackRequest.class)))
                .thenReturn(mockMetadata);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "content".getBytes());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("title", "Test Song");
        body.add("artist", "Artist");
        body.add("duration", "180");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Act
        ResponseEntity<AudioUploadResponse> response = restTemplate.exchange(
                "/api/v1/audio",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                AudioUploadResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(storageService).uploadAudio(any());
        verify(metadataClient).createTrack(any(TrackRequest.class));
        verify(eventProducer).sendAudioUploadEvent("track-123", "sources/test-key.mp3");
    }
}
