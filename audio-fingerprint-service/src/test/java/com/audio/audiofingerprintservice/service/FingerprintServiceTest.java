package com.audio.audiofingerprintservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.audio.audiofingerprintservice.TestUtils;
import com.audio.audiofingerprintservice.dto.*;
import com.audio.audiofingerprintservice.exception.FingerprintException;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FingerprintServiceTest {

    @Mock
    private ElasticsearchClient esClient;

    @Mock
    private ChromaprintWrapper chromaprint;

    @Mock
    private MetadataServiceClient metadataClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FingerprintService fingerprintService;

    @Test
    void searchMatch_ShouldReturnMatches() throws Exception {
        // Arrange
        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        when(chromaprint.generateFingerprint(any())).thenReturn("fingerprint");

        SearchResponse<AudioFingerprint> mockResponse = TestUtils.createMockSearchResponse();
        when(esClient.search(any(SearchRequest.class), eq(AudioFingerprint.class))).thenReturn(mockResponse);

        TrackMetadataResponse mockMetadata = new TrackMetadataResponse();
        mockMetadata.setTitle("Test");
        when(metadataClient.getTrackMetadata(any())).thenReturn(mockMetadata);

        // Act
        FingerprintMatchResponse result = fingerprintService.searchMatch(multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMatches().size());
    }

    @Test
    void searchMatch_ShouldThrowException() throws Exception {
        when(multipartFile.getBytes()).thenThrow(new IOException("Test error"));

        assertThrows(FingerprintException.class, () -> {
            fingerprintService.searchMatch(multipartFile);
        });
    }
}