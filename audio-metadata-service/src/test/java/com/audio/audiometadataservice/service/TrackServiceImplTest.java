package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.exception.TrackNotFoundException;
import com.audio.audiometadataservice.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private TrackServiceImpl trackService;

    private final Long TEST_ID = 1L;
    private final String TEST_AUDIO_KEY = "test-key";
    private final String PRESIGNED_URL = "http://presigned.url/test";

    @Test
    void getTrackById_ShouldReturnTrack_WhenExists() {
        // Arrange
        Track track = createTestTrack();
        TrackResponse expectedResponse = createTestResponse();

        when(trackRepository.findById(TEST_ID)).thenReturn(Optional.of(track));
        when(minioService.generatePresignedUrl(TEST_AUDIO_KEY)).thenReturn(PRESIGNED_URL);

        // Act
        TrackResponse result = trackService.getTrackById(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals("Test Track", result.getTitle());
        assertEquals(PRESIGNED_URL, result.getAudioUrl());
        verify(trackRepository).findById(TEST_ID);
        verify(minioService).generatePresignedUrl(TEST_AUDIO_KEY);
    }

    @Test
    void getTrackById_ShouldThrow_WhenNotExists() {
        // Arrange
        when(trackRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TrackNotFoundException.class, () ->
                trackService.getTrackById(TEST_ID));
    }

    @Test
    void createTrack_ShouldSaveAndReturnTrack() {
        // Arrange
        TrackRequest request = createTestRequest();
        Track savedTrack = createTestTrack();

        when(trackRepository.save(any(Track.class))).thenReturn(savedTrack);
        when(minioService.generatePresignedUrl(TEST_AUDIO_KEY)).thenReturn(PRESIGNED_URL);

        // Act
        TrackResponse result = trackService.createTrack(request);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals("Test Track", result.getTitle());
        assertEquals(PRESIGNED_URL, result.getAudioUrl());
        verify(trackRepository).save(any(Track.class));
        verify(minioService).generatePresignedUrl(TEST_AUDIO_KEY);
    }

    @Test
    void deleteTrack_ShouldDeleteTrack() {
        // Arrange
        doNothing().when(trackRepository).deleteById(TEST_ID);

        // Act
        trackService.deleteTrack(TEST_ID);

        // Assert
        verify(trackRepository).deleteById(TEST_ID);
    }

    @Test
    void findByAudioKey_ShouldReturnTrack_WhenExists() {
        // Arrange
        Track track = createTestTrack();

        when(trackRepository.findByAudioKey(TEST_AUDIO_KEY))
                .thenReturn(Optional.of(track));
        when(minioService.generatePresignedUrl(TEST_AUDIO_KEY)).thenReturn(PRESIGNED_URL);

        // Act
        TrackResponse result = trackService.findByAudioKey(TEST_AUDIO_KEY);

        // Assert
        assertEquals(TEST_ID, result.getId());
        assertEquals("Test Track", result.getTitle());
        assertEquals(PRESIGNED_URL, result.getAudioUrl());
        verify(trackRepository).findByAudioKey(TEST_AUDIO_KEY);
        verify(minioService).generatePresignedUrl(TEST_AUDIO_KEY);
    }

    @Test
    void findByAudioKey_ShouldThrow_WhenNotFound() {
        // Arrange
        when(trackRepository.findByAudioKey(TEST_AUDIO_KEY))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TrackNotFoundException.class, () ->
                trackService.findByAudioKey(TEST_AUDIO_KEY));
    }

    private TrackRequest createTestRequest() {
        return TrackRequest.builder()
                .title("Test Track")
                .artist("Test Artist")
                .audioKey(TEST_AUDIO_KEY)
                .duration(180)
                .build();
    }

    private Track createTestTrack() {
        return Track.builder()
                .id(TEST_ID)
                .title("Test Track")
                .artist("Test Artist")
                .audioKey(TEST_AUDIO_KEY)
                .duration(180)
                .build();
    }

    private TrackResponse createTestResponse() {
        return TrackResponse.builder()
                .id(TEST_ID)
                .title("Test Track")
                .artist("Test Artist")
                .duration(180)
                .audioUrl(PRESIGNED_URL)
                .build();
    }
}