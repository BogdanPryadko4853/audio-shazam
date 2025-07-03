package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import com.audio.audiofingerprintservice.model.AudioUploadEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MinioService minioService;

    @Mock
    private FingerprintService fingerprintService;

    @Mock
    private MetadataServiceClient metadataClient;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void consumeAudioUpload_ShouldProcessSuccessfully() throws Exception {
        // Arrange
        AudioUploadEvent event = new AudioUploadEvent();
        event.setTrackId("123");
        event.setS3Key("test.mp3");

        when(objectMapper.readValue(anyString(), eq(AudioUploadEvent.class))).thenReturn(event);
        when(minioService.downloadAudio(any())).thenReturn(new byte[0]);
        when(fingerprintService.generateFingerprint(any())).thenReturn("fp");
        when(metadataClient.getTrackMetadata(any())).thenReturn(new TrackMetadataResponse());

        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", "value");

        // Act
        kafkaConsumerService.consumeAudioUpload(record);

        // Assert
        verify(fingerprintService).saveFingerprint(any(AudioFingerprint.class));
    }

    @Test
    void consumeAudioUpload_ShouldHandleError() throws Exception {
        when(objectMapper.readValue(anyString(), any(Class.class))).thenThrow(new JsonProcessingException("Error") {});

        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", "value");

        kafkaConsumerService.consumeAudioUpload(record);

        verify(fingerprintService, never()).saveFingerprint(any());
    }
}
