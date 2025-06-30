package com.audio.audiofingerprintservice.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("AudioFingerprint")
public class FingerprintHash {
    @Id
    private String audioId;

    private String fingerprintHash;
    private Instant createdAt;
}

