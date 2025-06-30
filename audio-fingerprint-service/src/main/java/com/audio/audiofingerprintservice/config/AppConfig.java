package com.audio.audiofingerprintservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fingerprint")
@Data
public class AppConfig {
    private int sampleRate;
    private int windowSize;
    private double minAmplitude;
}
