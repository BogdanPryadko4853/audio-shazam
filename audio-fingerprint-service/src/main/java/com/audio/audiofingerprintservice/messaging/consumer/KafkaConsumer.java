package com.audio.audiofingerprintservice.messaging.consumer;

import com.audio.audiofingerprintservice.client.MetadataServiceClient;
import com.audio.audiofingerprintservice.dto.response.TrackMetadataResponse;
import com.audio.audiofingerprintservice.messaging.event.AudioUploadEvent;
import com.audio.audiofingerprintservice.model.*;
import com.audio.audiofingerprintservice.service.FingerprintService;
import com.audio.audiofingerprintservice.service.MinioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

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