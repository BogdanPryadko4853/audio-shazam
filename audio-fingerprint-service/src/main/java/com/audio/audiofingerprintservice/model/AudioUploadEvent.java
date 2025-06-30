package com.audio.audiofingerprintservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioUploadEvent {
    private String eventId;
    private String s3Key;
    private Instant timestamp;
}
