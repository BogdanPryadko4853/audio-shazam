package com.audio.audioingestionservice.service;

import com.audio.audioingestionservice.model.AudioUploadEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AudioEventProducerTest {

    @Mock
    private KafkaTemplate<String, AudioUploadEvent> kafkaTemplate;

    @InjectMocks
    private AudioEventProducer eventProducer;

    @Test
    void shouldSendAudioUploadEvent() {
        // Act
        eventProducer.sendAudioUploadEvent("test-key");

        // Assert
        verify(kafkaTemplate).send(eq("audio-uploads"), any(AudioUploadEvent.class));
    }
}
