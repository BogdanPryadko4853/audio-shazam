package com.audio.audiofingerprintservice.dto;

import lombok.Data;

@Data
public class AudioMatch {
    private String trackId;
    private String title;
    private String artist;
    private Integer duration;
    private String audioUrl;
    private float confidence;
}
