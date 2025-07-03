package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MetadataServiceClient metadataServiceClient;

    @Test
    void getTrackMetadata_ShouldReturnMetadata() {
        TrackMetadataResponse expected = new TrackMetadataResponse();
        expected.setTitle("Test Track");

        when(restTemplate.getForObject(anyString(), eq(TrackMetadataResponse.class)))
                .thenReturn(expected);

        TrackMetadataResponse result = metadataServiceClient.getTrackMetadata("123");

        assertEquals("Test Track", result.getTitle());
    }
}