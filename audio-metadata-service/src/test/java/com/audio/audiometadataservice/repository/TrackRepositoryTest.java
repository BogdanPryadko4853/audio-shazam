package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.entity.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    @Test
    @Sql("classpath:test-data.sql")
    void findByAudioKey_ShouldReturnTrack_WhenExists() {
        // Act
        Optional<Track> result = trackRepository.findByAudioKey("test-key-1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Track 1", result.get().getTitle());
    }

    @Test
    void findByAudioKey_ShouldReturnEmpty_WhenNotExists() {
        // Act
        Optional<Track> result = trackRepository.findByAudioKey("non-existent-key");

        // Assert
        assertFalse(result.isPresent());
    }
}