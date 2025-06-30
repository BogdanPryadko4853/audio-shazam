package com.audio.audiofingerprintservice.messaging;

import com.audio.audiofingerprintservice.model.AudioUploadEvent;
import com.audio.audiofingerprintservice.service.FingerprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AudioUploadListener {
    private final FingerprintService fingerprintService;

    @KafkaListener(topics = "audio-uploads")
    public void handleAudioUpload(AudioUploadEvent event) {
        try {
            fingerprintService.processAudio(event.getAudioId(), event.getS3Path());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process audio: " + event.getAudioId(), e);
        }
    }
}
