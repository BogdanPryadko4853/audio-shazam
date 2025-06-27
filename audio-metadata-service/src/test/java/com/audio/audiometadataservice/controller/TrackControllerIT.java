package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.repository.TrackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TrackControllerIT {

    private static final int REDIS_PORT = 6379;

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2-alpine")
                    .withExposedPorts(6379);

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
        registry.add("spring.data.redis.timeout", () -> "5000ms");

        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrackRepository trackRepository;

    @AfterEach
    void tearDown() {
        trackRepository.deleteAll();
    }

    @Test
    void createTrack_ShouldReturnCreatedTrack() throws Exception {
        // Arrange
        String requestBody = """
        {
            "title": "Integration Test Track",
            "artist": "Test Artist",
            "audioKey": "integration-test-key",
            "album": "Test Album",
            "duration": 240,
            "releaseDate": "2023-01-01"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Track")))
                .andExpect(jsonPath("$.audioKey", is("integration-test-key")));
    }

    @Test
    void getTrack_ShouldReturnTrack_WhenExists() throws Exception {
        // Arrange
        Track track = new Track();
        track.setTitle("Test Track");
        track.setArtist("Test Artist");
        track.setAudioKey("test-key");
        track.setDuration(180);
        track.setReleaseDate(LocalDate.now());
        Track saved = trackRepository.save(track);

        // Act & Assert
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