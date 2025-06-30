package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.model.FingerprintProcessedEvent;
import com.audio.audiofingerprintservice.model.entity.FingerprintHash;
import com.audio.audiofingerprintservice.model.entity.FingerprintMetadata;
import com.audio.audiofingerprintservice.repository.FingerprintHashRepository;
import com.audio.audiofingerprintservice.repository.FingerprintMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FingerprintService {
    private final AudioProcessor audioProcessor;
    private final FingerprintGenerator fingerprintGenerator;
    private final FingerprintHashRepository hashRepository;
    private final FingerprintMetadataRepository metadataRepository;
    private final KafkaTemplate<String, FingerprintProcessedEvent> kafkaTemplate;

    @Transactional
    public void processAudio(String audioId, String s3Path) throws Exception {
        byte[] audioData = audioProcessor.downloadAudio(s3Path);

        String fingerprintHash = fingerprintGenerator.generateFingerprint(audioData);

        hashRepository.save(new FingerprintHash(
                audioId,
                fingerprintHash,
                Instant.now()
        ));

        metadataRepository.save(new FingerprintMetadata(audioId));

        kafkaTemplate.send("fingerprint-processed",
                new FingerprintProcessedEvent(
                        audioId,
                        fingerprintHash,
                        Instant.now()
                ));
    }
}