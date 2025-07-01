package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.dto.AudioUploadResponse;
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
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        when(metadataClient.createTrack(any(), any(), any(), any(), any()))
                .thenReturn(TrackMetadata.builder()
                        .id("track-123")
                        .title("Test Song")
                        .artist("Artist")
                        .duration(180)
                        .audioPath("sources/test-key.mp3")
                        .createdAt(LocalDateTime.now())
                        .build());

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
        verify(metadataClient).createTrack(eq("Test Song"), eq("Artist"), eq(null), eq(180), eq("sources/test-key.mp3"));
        verify(eventProducer).sendAudioUploadEvent(eq("track-123"), eq("sources/test-key.mp3"), eq("Test Song"), eq("Artist"));
    }
}
