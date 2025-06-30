package com.audio.audiofingerprintservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Peak {
    private double frequency;
    private double amplitude;
    private double timeOffset;
}
