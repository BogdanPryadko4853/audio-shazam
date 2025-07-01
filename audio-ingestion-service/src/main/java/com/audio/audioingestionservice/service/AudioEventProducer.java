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
    private final KafkaTemplate<String, AudioUploadEvent> kafkaTemplate;

    public void sendAudioUploadEvent(String trackId, String s3Key, String title, String artist) {
        AudioUploadEvent event = new AudioUploadEvent(
                trackId,
                s3Key,
                title,
                artist,
                Instant.now()
        );
        kafkaTemplate.send("audio-uploads", event);
    }
}

