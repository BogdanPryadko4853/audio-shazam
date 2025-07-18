package com.audio.audioingestionservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioUploadEvent {
    private String trackId;
    private String s3Key;
}
