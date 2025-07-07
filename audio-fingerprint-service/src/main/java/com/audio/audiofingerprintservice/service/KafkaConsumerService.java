package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import com.audio.audiofingerprintservice.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final MinioService minioService;
    private final FingerprintService fingerprintService;
    private final MetadataServiceClient metadataClient;

    @KafkaListener(topics = "audio-uploads")
    public void consumeAudioUpload(ConsumerRecord<String, String> record) {
        try {
            AudioUploadEvent event = objectMapper.readValue(record.value(), AudioUploadEvent.class);
            TrackMetadataResponse metadata = metadataClient.getTrackMetadata(event.getTrackId());
            byte[] audioData = minioService.downloadAudio(event.getS3Key());

            AudioFingerprint fingerprint = AudioFingerprint.builder()
                    .trackId(event.getTrackId())
                    .title(metadata.getTitle())
                    .artist(metadata.getArtist())
                    .fingerprint(fingerprintService.generateFingerprint(audioData))
                    .build();

            fingerprintService.saveFingerprint(fingerprint);
            log.info("Successfully processed audio upload for track: {}", event.getTrackId());
        } catch (Exception e) {
            log.error("Failed to process audio upload: {}", e.getMessage(), e);
        }
    }
}