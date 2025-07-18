package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.dto.request.TrackRequest;
import com.audio.audiometadataservice.dto.response.TrackResponse;
import com.audio.audiometadataservice.service.TrackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackControllerTest {

    @Mock
    private TrackService trackService;
    @InjectMocks
    private TrackController trackController;

    private final Long TEST_ID = 1L;

    @Test
    void getTrackByAudioKey_ShouldReturnOk() {
        TrackResponse response = new TrackResponse();
        when(trackService.findByAudioKey("test-key")).thenReturn(response);

        ResponseEntity<TrackResponse> result = trackController.getTrackByAudioKey("test-key");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void createTrack_ShouldReturnCreated() {
        // Arrange
        TrackRequest request = new TrackRequest();
        TrackResponse response = new TrackResponse();
        when(trackService.createTrack(any(TrackRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<TrackResponse> result = trackController.createTrack(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteTrack_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(trackService).deleteTrack(TEST_ID);

        // Act
        ResponseEntity<Void> result = trackController.deleteTrack(TEST_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(trackService).deleteTrack(TEST_ID);
    }
}