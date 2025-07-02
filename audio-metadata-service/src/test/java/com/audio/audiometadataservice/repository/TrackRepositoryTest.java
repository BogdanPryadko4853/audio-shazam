package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.entity.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TrackRepositoryTest {

    // 1. Add PostgreSQL container definition
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // 2. Configure dynamic properties
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TrackRepository trackRepository;

    @Test
    void findByAudioKey_ShouldReturnTrack_WhenExists() {
        Track track = new Track();
        track.setTitle("Test Track");
        track.setArtist("Test Artist");
        track.setAudioKey("test-key-1");
        trackRepository.save(track);

        Optional<Track> result = trackRepository.findByAudioKey("test-key-1");

        assertTrue(result.isPresent());
        assertEquals("Test Track", result.get().getTitle());
    }

    @Test
    void findByAudioKey_ShouldReturnEmpty_WhenNotExists() {
        Optional<Track> result = trackRepository.findByAudioKey("non-existent-key");
        assertFalse(result.isPresent());
    }
}