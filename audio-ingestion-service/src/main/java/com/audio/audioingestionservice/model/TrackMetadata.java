package com.audio.audioingestionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackMetadata {
    private String id;
    private String title;
    private String artist;
    private Integer duration;
    private String audioPath;
    private LocalDateTime createdAt;
}
