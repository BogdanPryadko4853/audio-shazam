package com.audio.audioingestionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AudioUploadResponse {
    private String storageKey;
    private String message;

    public AudioUploadResponse(String storageKey, String message) {
        this.storageKey = storageKey;
        this.message = message;
    }
}