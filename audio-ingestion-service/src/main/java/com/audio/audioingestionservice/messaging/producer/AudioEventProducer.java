package com.audio.audioingestionservice.messaging.producer;

import com.audio.audioingestionservice.messaging.event.AudioUploadEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AudioEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendAudioUploadEvent(String trackId, String s3Key) {
        AudioUploadEvent event = new AudioUploadEvent(trackId, s3Key);
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("audio-uploads", jsonEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}

