package com.audio.audiometadataservice.service.mapper;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.mapper.TrackMapper;
import com.audio.audiometadataservice.model.TrackCache;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrackMapperTest {

    private final TrackMapper mapper = Mappers.getMapper(TrackMapper.class);
    private final LocalDate TEST_DATE = LocalDate.now();
    private final LocalDateTime TEST_DATE_TIME = LocalDateTime.now();

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Arrange
        TrackRequest request = new TrackRequest();
        request.setTitle("Test");
        request.setArtist("Artist");
        request.setAudioKey("key");
        request.setAlbum("Album");
        request.setDuration(180);
        request.setReleaseDate(TEST_DATE);

        // Act
        Track result = mapper.toEntity(request);

        // Assert
        assertEquals("Test", result.getTitle());
        assertEquals("Artist", result.getArtist());
        assertEquals("key", result.getAudioKey());
        assertEquals("Album", result.getAlbum());
        assertEquals(180, result.getDuration());
        assertEquals(TEST_DATE, result.getReleaseDate());
    }

    @Test
    void toResponse_ShouldMapCorrectly() {
        // Arrange
        Track track = new Track();
        track.setId(1L);
        track.setTitle("Test");
        track.setArtist("Artist");
        track.setAudioKey("key");
        track.setAlbum("Album");
        track.setDuration(180);
        track.setReleaseDate(TEST_DATE);
        track.setCreatedAt(TEST_DATE_TIME);

        // Act
        TrackResponse result = mapper.toResponse(track);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getTitle());
        assertEquals("Artist", result.getArtist());
        assertEquals("key", result.getAudioKey());
        assertEquals("Album", result.getAlbum());
        assertEquals(180, result.getDuration());
        assertEquals(TEST_DATE, result.getReleaseDate());
        assertEquals(TEST_DATE_TIME, result.getCreatedAt());
    }

    @Test
    void toCache_ShouldMapCorrectly() {
        // Arrange
        Track track = new Track();
        track.setId(1L);
        track.setTitle("Test");
        track.setArtist("Artist");
        track.setAudioKey("key");
        track.setAlbum("Album");
        track.setDuration(180);
        track.setReleaseDate(TEST_DATE);
        track.setCreatedAt(TEST_DATE_TIME);

        // Act
        TrackCache result = mapper.toCache(track);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getTitle());
        assertEquals("Artist", result.getArtist());
        assertEquals("key", result.getAudioKey());
        assertEquals("Album", result.getAlbum());
        assertEquals(180, result.getDuration());
        assertEquals(TEST_DATE, result.getReleaseDate());
        assertEquals(TEST_DATE_TIME, result.getCreatedAt());
    }
}