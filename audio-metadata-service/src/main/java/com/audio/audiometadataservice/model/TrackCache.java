package com.audio.audiometadataservice.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Track")
public class TrackCache {
    private Long id;
    private String title;
    private String artist;
    private String audioKey;
    private String album;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;
}