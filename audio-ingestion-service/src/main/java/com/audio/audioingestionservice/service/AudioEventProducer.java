package com.audio.audioingestionservice.service;

import com.audio.audioingestionservice.model.AudioUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AudioEventProducer {
    private final KafkaTemplate<String, AudioUploadEvent> kafkaTemplate;

    public void sendAudioUploadEvent(String trackId, String s3Key) {
        AudioUploadEvent event = new AudioUploadEvent(
                trackId,
                s3Key
        );
        kafkaTemplate.send("audio-uploads", event);
    }
}

