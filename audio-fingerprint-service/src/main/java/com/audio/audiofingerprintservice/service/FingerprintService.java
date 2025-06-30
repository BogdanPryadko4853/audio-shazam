package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.model.AudioUploadEvent;
import com.audio.audiofingerprintservice.model.entity.FingerprintHash;
import com.audio.audiofingerprintservice.model.entity.FingerprintMetadata;
import com.audio.audiofingerprintservice.repository.FingerprintHashRepository;
import com.audio.audiofingerprintservice.repository.FingerprintMetadataRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class FingerprintService {
    private final MinioClient minioClient;
    private final FingerprintGenerator fingerprintGenerator;
    private final FingerprintHashRepository hashRepository;
    private final FingerprintMetadataRepository metadataRepository;

    @Value("${minio.bucket}")
    private String bucket;

    public void processAudioUpload(AudioUploadEvent event) {
        try {
            byte[] audioData = downloadAudio(event.getS3Key());

            String fingerprint = fingerprintGenerator.generate(audioData);

            hashRepository.save(new FingerprintHash(
                    event.getEventId(),
                    fingerprint,
                    Instant.now()
            ));

            FingerprintMetadata metadata = new FingerprintMetadata(event.getEventId());
            metadata.setTrackTitle("Unknown");
            metadata.setArtist("Unknown");
            metadataRepository.save(metadata);

            log.info("Processed audio: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing audio: {}", event.getEventId(), e);
            throw new RuntimeException(e);
        }
    }

    private byte[] downloadAudio(String s3Key) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(s3Key)
                        .build())) {
            return stream.readAllBytes();
        }
    }
}