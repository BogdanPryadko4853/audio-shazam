package com.audio.audiofingerprintservice.messaging;

import com.audio.audiofingerprintservice.model.AudioUploadEvent;
import com.audio.audiofingerprintservice.service.FingerprintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AudioUploadListener {
    private final FingerprintService fingerprintService;

    @KafkaListener(topics = "${kafka.topics.audio-uploads}")
    public void listen(AudioUploadEvent event) {
        log.info("Received audio upload event: {}", event.getEventId());
        fingerprintService.processAudioUpload(event);
    }
}
