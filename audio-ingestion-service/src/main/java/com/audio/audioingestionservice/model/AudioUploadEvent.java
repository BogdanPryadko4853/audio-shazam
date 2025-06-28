package com.audio.audioingestionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AudioUploadEvent {
    private String eventId;
    private String s3Key;
    private Instant timestamp;
}
