package com.audio.audiofingerprintservice.model;

import lombok.Data;

@Data
public class AudioUploadEvent {
    private String trackId;
    private String s3Key;
}
