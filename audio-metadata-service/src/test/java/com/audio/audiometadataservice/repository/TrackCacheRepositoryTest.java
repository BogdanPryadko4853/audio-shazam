package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.model.TrackCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@Testcontainers
class TrackCacheRepositoryTest {

    private static final int REDIS_PORT = 6379;

    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                    .withExposedPorts(REDIS_PORT)
                    .withReuse(true);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.bootstrap.enabled", () -> false);
        registry.add("spring.cloud.config.import-check.enabled", () -> false);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
        registry.add("spring.data.redis.timeout", () -> "5000ms");
    }

    @Autowired
    private TrackCacheRepository trackCacheRepository;

    @BeforeEach
    void clearData() {
        trackCacheRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindByAudioKey() {
        TrackCache track = new TrackCache();
        track.setId(1L);
        track.setAudioKey("test-key-123");
        track.setTitle("Test Track");

        TrackCache saved = trackCacheRepository.save(track);
        Optional<TrackCache> byAudioKey = trackCacheRepository.findByAudioKey("test-key-123");

        assertTrue(byAudioKey.isPresent());
        assertEquals(saved.getId(), byAudioKey.get().getId());
    }
}