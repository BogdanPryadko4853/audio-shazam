package com.audio.audiofingerprintservice.model;

import lombok.Data;

import java.util.List;

@Data
public class AudioFingerprint {
    private String trackId;
    private String title;
    private String artist;
    private List<Float> fingerprint; // Изменили String на List<Float>
}
