package com.audio.audiometadataservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String artist;

    @NotBlank
    private String audioKey;

    private String album;
    private Integer duration;
    private LocalDate releaseDate;
}
