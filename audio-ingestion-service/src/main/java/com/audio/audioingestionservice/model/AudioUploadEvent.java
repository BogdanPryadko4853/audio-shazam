package com.audio.audioingestionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AudioUploadEvent {
    private String trackId;
    private String s3Key;
}
