package com.audio.audioingestionservice.service;

import com.audio.audioingestionservice.messaging.event.AudioUploadEvent;
import com.audio.audioingestionservice.messaging.producer.AudioEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AudioEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AudioEventProducer eventProducer;

    @Test
    void shouldSendCompleteAudioUploadEvent() throws JsonProcessingException {
        // Arrange
        AudioUploadEvent expectedEvent = new AudioUploadEvent("track-123", "sources/test.mp3");
        when(objectMapper.writeValueAsString(expectedEvent)).thenReturn("serialized-event");

        // Act
        eventProducer.sendAudioUploadEvent("track-123", "sources/test.mp3");

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> eventCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("audio-uploads");
        assertThat(eventCaptor.getValue()).isEqualTo("serialized-event");
    }
}