package com.audio.audiofingerprintservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FingerprintProcessedEvent {
    private String audioId;
    private String fingerprintHash;
    private Instant processedAt;
}