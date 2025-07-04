package com.audio.audiometadataservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponse {
    private Long id;
    private String title;
    private String artist;
    private String audioUrl;
    private Integer duration;
}