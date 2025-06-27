package com.audio.audiometadataservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponse {
    private Long id;
    private String title;
    private String artist;
    private String audioKey;
    private String album;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;
}