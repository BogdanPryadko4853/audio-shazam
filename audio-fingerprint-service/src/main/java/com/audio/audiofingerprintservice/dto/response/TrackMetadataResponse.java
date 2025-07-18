package com.audio.audiofingerprintservice.dto.response;

import lombok.Data;

@Data
public class TrackMetadataResponse {
    private String id;
    private String title;
    private String artist;
    private Integer duration;
    private String audioUrl;
}