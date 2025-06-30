package com.audio.audiofingerprintservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioUploadEvent {
    private String audioId;
    private String s3Path;
}
