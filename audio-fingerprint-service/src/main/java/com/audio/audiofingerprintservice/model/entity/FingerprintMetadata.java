package com.audio.audiofingerprintservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class FingerprintMetadata {
    @Id
    private String id;

    private String audioId;
    private String trackTitle;
    private String artist;
    private Instant processedAt;

    public FingerprintMetadata(String audioId) {
        this.id = UUID.randomUUID().toString();
        this.audioId = audioId;
        this.processedAt = Instant.now();
    }
}
