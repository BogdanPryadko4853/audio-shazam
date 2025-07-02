package com.audio.audiometadataservice.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
    @Indexed
    private String audioKey;
    private Integer duration;
}