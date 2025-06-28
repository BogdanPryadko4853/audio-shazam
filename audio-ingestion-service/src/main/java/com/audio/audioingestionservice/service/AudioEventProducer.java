package com.audio.audioingestionservice.service;

import com.audio.audioingestionservice.model.AudioUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioEventProducer {
    private static final String TOPIC = "audio-uploads";

    private final KafkaTemplate<String, AudioUploadEvent> kafkaTemplate;

    public void sendAudioUploadEvent(String s3Key) {
        AudioUploadEvent event = new AudioUploadEvent(
                UUID.randomUUID().toString(),
                s3Key,
                Instant.now()
        );

        kafkaTemplate.send(TOPIC, event);
    }
}

