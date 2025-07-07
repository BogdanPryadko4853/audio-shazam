package com.audio.audiofingerprintservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FingerprintMatchResponse {
    private List<AudioMatch> matches;
}
