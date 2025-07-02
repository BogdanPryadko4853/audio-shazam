package com.audio.audiofingerprintservice.model;

import lombok.Data;

@Data
public class AudioFingerprint {
    private String trackId;
    private String title;
    private String artist;
    private String fingerprint;
}
