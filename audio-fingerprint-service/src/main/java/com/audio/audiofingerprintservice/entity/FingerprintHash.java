package com.audio.audiofingerprintservice.entity;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@RedisHash("AudioFingerprint")
public class FingerprintHash {
    @Id
    private String audioId;
    private String trackId;
    private String hash;
    private Instant createdAt;
}

