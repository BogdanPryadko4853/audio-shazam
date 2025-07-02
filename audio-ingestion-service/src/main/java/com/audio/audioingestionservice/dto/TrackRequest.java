package com.audio.audioingestionservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackRequest {
    private String title;
    private String artist;
    private Integer duration;
    private String audioKey;
}

