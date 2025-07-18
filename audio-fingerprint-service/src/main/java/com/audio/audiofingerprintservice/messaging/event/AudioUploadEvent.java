package com.audio.audiofingerprintservice.messaging.event;

import lombok.Data;

@Data
public class AudioUploadEvent {
    private String trackId;
    private String s3Key;
}
