package com.audio.audiometadataservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    private String album;
    private Integer duration;
    @NotBlank
    private String audioKey;
}
