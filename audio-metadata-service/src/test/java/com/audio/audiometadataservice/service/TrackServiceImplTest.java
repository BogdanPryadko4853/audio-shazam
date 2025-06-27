package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.exception.TrackNotFoundException;
import com.audio.audiometadataservice.mapper.TrackMapper;
import com.audio.audiometadataservice.model.TrackCache;
import com.audio.audiometadataservice.repository.TrackCacheRepository;
import com.audio.audiometadataservice.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplTest {

    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackCacheRepository trackCacheRepository;
    @Mock
    private TrackMapper trackMapper;

    @InjectMocks
    private com.audio.audiometadataservice.service.impl.TrackServiceImpl trackService;

    private final Long TEST_ID = 1L;
    private final String TEST_AUDIO_KEY = "test-key";

    @Test
    void getTrackById_ShouldReturnTrack_WhenExists() {
        // Arrange
        Track track = createTestTrack();
        TrackResponse expectedResponse = createTestResponse();

        when(trackRepository.findById(TEST_ID)).thenReturn(Optional.of(track));
        when(trackMapper.toResponse(track)).thenReturn(expectedResponse);

        // Act
        TrackResponse result = trackService.getTrackById(TEST_ID);

        // Assert
        assertEquals(TEST_ID, result.getId());
        verify(trackRepository).findById(TEST_ID);
        verify(trackMapper).toResponse(track);
    }

    @Test
    void getTrackById_ShouldThrow_WhenNotExists() {
        // Arrange
        when(trackRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TrackNotFoundException.class,
                () -> trackService.getTrackById(TEST_ID));
    }

    @Test
    void createTrack_ShouldSaveAndReturnTrack() {
        // Arrange
        TrackRequest request = createTestRequest();
        Track track = createTestTrack();
        Track savedTrack = createTestTrack();
        TrackResponse expectedResponse = createTestResponse();

        when(trackMapper.toEntity(request)).thenReturn(track);
        when(trackRepository.save(track)).thenReturn(savedTrack);
        when(trackMapper.toResponse(savedTrack)).thenReturn(expectedResponse);
        when(trackMapper.toCache(savedTrack)).thenReturn(new TrackCache());

        // Act
        TrackResponse result = trackService.createTrack(request);

        // Assert
        assertNotNull(result);
        verify(trackRepository).save(track);
        verify(trackCacheRepository).save(any(TrackCache.class));
    }

    @Test
    void deleteTrack_ShouldDeleteFromBothRepositories() {
        // Arrange
        doNothing().when(trackRepository).deleteById(TEST_ID);
        doNothing().when(trackCacheRepository).deleteById(TEST_ID);

        // Act
        trackService.deleteTrack(TEST_ID);

        // Assert
        verify(trackRepository).deleteById(TEST_ID);
        verify(trackCacheRepository).deleteById(TEST_ID);
    }

    @Test
    void findByAudioKey_ShouldReturnFromCache_WhenExists() {
        // Arrange
        TrackCache cachedTrack = new TrackCache();
        cachedTrack.setId(TEST_ID);
        TrackResponse expectedResponse = createTestResponse();

        when(trackCacheRepository.findByAudioKey(TEST_AUDIO_KEY))
                .thenReturn(Optional.of(cachedTrack));
        when(trackMapper.toResponse(cachedTrack)).thenReturn(expectedResponse);

        // Act
        TrackResponse result = trackService.findByAudioKey(TEST_AUDIO_KEY);

        // Assert
        assertEquals(TEST_ID, result.getId());
        verify(trackCacheRepository).findByAudioKey(TEST_AUDIO_KEY);
        verifyNoInteractions(trackRepository);
    }

    @Test
    void findByAudioKey_ShouldFetchFromDbAndCache_WhenNotInCache() {
        // Arrange
        Track track = createTestTrack();
        TrackResponse expectedResponse = createTestResponse();

        when(trackCacheRepository.findByAudioKey(TEST_AUDIO_KEY))
                .thenReturn(Optional.empty());
        when(trackRepository.findByAudioKey(TEST_AUDIO_KEY))
                .thenReturn(Optional.of(track));
        when(trackMapper.toResponse(track)).thenReturn(expectedResponse);
        when(trackMapper.toCache(track)).thenReturn(new TrackCache());

        // Act
        TrackResponse result = trackService.findByAudioKey(TEST_AUDIO_KEY);

        // Assert
        assertEquals(TEST_ID, result.getId());
        verify(trackCacheRepository).save(any(TrackCache.class));
    }

    private TrackRequest createTestRequest() {
        return TrackRequest.builder()
                .title("Test Track")
                .artist("Test Artist")
                .audioKey(TEST_AUDIO_KEY)
                .album("Test Album")
                .duration(180)
                .releaseDate(LocalDate.now())
                .build();
    }

    private Track createTestTrack() {
        Track track = new Track();
        track.setId(TEST_ID);
        track.setTitle("Test Track");
        track.setArtist("Test Artist");
        track.setAudioKey(TEST_AUDIO_KEY);
        track.setCreatedAt(LocalDateTime.now());
        return track;
    }

    private TrackResponse createTestResponse() {
        return TrackResponse.builder()
                .id(TEST_ID)
                .title("Test Track")
                .artist("Test Artist")
                .audioKey(TEST_AUDIO_KEY)
                .createdAt(LocalDateTime.now())
                .build();
    }
}