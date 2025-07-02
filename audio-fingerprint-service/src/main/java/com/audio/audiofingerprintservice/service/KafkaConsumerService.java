package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import com.audio.audiofingerprintservice.model.AudioUploadEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final MinioService minioService;
    private final FingerprintService fingerprintService;
    private final MetadataServiceClient metadataClient;



    @KafkaListener(topics = "audio-uploads")
    public void consumeAudioUpload(ConsumerRecord<String, String> record) {
        try {
            AudioUploadEvent event = objectMapper.readValue(record.value(), AudioUploadEvent.class);

            // 1. Получаем метаданные из сервиса
            TrackMetadataResponse metadata = metadataClient.getTrackMetadata(event.getTrackId());

            // 2. Загружаем аудио
            byte[] audioData = minioService.downloadAudio(event.getS3Key());

            // 3. Генерируем fingerprint
            String fingerprint = fingerprintService.generateFingerprint(audioData);

            // 4. Сохраняем
            AudioFingerprint audioFingerprint = new AudioFingerprint();
            audioFingerprint.setTrackId(event.getTrackId());
            audioFingerprint.setTitle(metadata.getTitle());
            audioFingerprint.setArtist(metadata.getArtist());
            audioFingerprint.setFingerprint(fingerprint);

            fingerprintService.saveFingerprint(audioFingerprint);
        } catch (Exception e) {
            logger.error("Processing failed: {}", e.getMessage());
        }
    }
}