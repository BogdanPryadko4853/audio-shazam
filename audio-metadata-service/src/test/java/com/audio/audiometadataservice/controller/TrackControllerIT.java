package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.repository.TrackRepository;
import com.audio.audiometadataservice.service.MinioService;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class TrackControllerIT {

    private static final int REDIS_PORT = 6379;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(REDIS_PORT);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
        registry.add("spring.data.redis.timeout", () -> "5000ms");

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("minio.url", () -> "http://localhost:9000");
        registry.add("minio.access-key", () -> "test-access-key");
        registry.add("minio.secret-key", () -> "test-secret-key");
        registry.add("minio.bucket", () -> "test-bucket");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrackRepository trackRepository;

    @MockBean
    private MinioService minioService;

    @BeforeEach
    void setUp() {
        when(minioService.generatePresignedUrl(anyString()))
                .thenReturn("http://presigned.url/test");
    }

    @AfterEach
    void tearDown() {
        trackRepository.deleteAll();
    }

    @Test
    void createTrack_ShouldReturnCreatedTrack() throws Exception {
        String requestBody = """
                {
                    "title": "Integration Test Track",
                    "artist": "Test Artist",
                    "audioKey": "test-key-123",
                    "duration": 240
                }
                """;


        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Track")))
                .andExpect(jsonPath("$.audioUrl", notNullValue()));
    }

    @Test
    void getTrack_ShouldReturnTrack_WhenExists() throws Exception {
        Track track = new Track();
        track.setTitle("Test Track");
        track.setArtist("Test Artist");
        track.setAudioKey("test-key");
        track.setDuration(180);
        Track saved = trackRepository.save(track);

        mockMvc.perform(get("/api/v1/tracks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Track")));
    }

    @Test
    void getTrack_ShouldReturn404_WhenNotExists() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tracks/999"))
                .andExpect(status().isNotFound());
    }
}