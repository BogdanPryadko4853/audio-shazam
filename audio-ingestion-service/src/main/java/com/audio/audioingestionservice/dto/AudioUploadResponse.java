package com.audio.audioingestionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioUploadResponse {
    private String storageKey;
    private String message;
}