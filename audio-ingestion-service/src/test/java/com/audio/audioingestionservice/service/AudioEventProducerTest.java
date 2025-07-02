package com.audio.audioingestionservice.service;

import com.audio.audioingestionservice.model.AudioUploadEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AudioEventProducerTest {

    @Mock
    private KafkaTemplate<String, AudioUploadEvent> kafkaTemplate;

    @InjectMocks
    private AudioEventProducer eventProducer;

    @Test
    void shouldSendCompleteAudioUploadEvent() {
        // Act
        eventProducer.sendAudioUploadEvent("track-123", "sources/test.mp3");

        // Assert
        ArgumentCaptor<AudioUploadEvent> eventCaptor = ArgumentCaptor.forClass(AudioUploadEvent.class);
        verify(kafkaTemplate).send(eq("audio-uploads"), eventCaptor.capture());

        AudioUploadEvent event = eventCaptor.getValue();
        assertThat(event.getTrackId()).isEqualTo("track-123");
        assertThat(event.getS3Key()).isEqualTo("sources/test.mp3");
    }
}
