package com.audio.audiofingerprintservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class FingerprintMatchResponse {
    private List<AudioMatch> matches;
}
