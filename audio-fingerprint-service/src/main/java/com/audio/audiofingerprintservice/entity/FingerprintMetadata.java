package com.audio.audiofingerprintservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class FingerprintMetadata {
    @Id
    private String id;
    private String audioId;
    private String trackTitle;
    private String artist;
    private Instant processedAt;
}
