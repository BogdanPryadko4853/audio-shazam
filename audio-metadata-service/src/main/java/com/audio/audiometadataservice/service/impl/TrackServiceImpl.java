package com.audio.audiometadataservice.service.impl;

import com.audio.audiometadataservice.dto.request.TrackRequest;
import com.audio.audiometadataservice.dto.response.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.exception.TrackNotFoundException;
import com.audio.audiometadataservice.repository.TrackRepository;
import com.audio.audiometadataservice.service.MinioService;
import com.audio.audiometadataservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final MinioService minioService;

    private static final long CACHE_TTL_MINUTES = 30;

    @Override
    @Cacheable(value = "tracks", key = "#id")
    public TrackResponse getTrackById(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException("Track not found with id: " + id));

        return buildResponse(track);
    }

    @Override
    @CacheEvict(value = "tracks", allEntries = true)
    public TrackResponse createTrack(TrackRequest request) {
        Track track = Track.builder()
                .title(request.getTitle())
                .artist(request.getArtist())
                .duration(request.getDuration())
                .audioKey(request.getAudioKey())
                .build();

        Track savedTrack = trackRepository.save(track);
        return buildResponse(savedTrack);
    }

    @Override
    @CacheEvict(value = "tracks", key = "#id")
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "tracks", key = "'audioKey:' + #audioKey")
    public TrackResponse findByAudioKey(String audioKey) {
        Track track = trackRepository.findByAudioKey(audioKey)
                .orElseThrow(() -> new TrackNotFoundException("Track not found with audio key: " + audioKey));

        return buildResponse(track);
    }

    private TrackResponse buildResponse(Track track) {
        String audioUrl = minioService.generatePresignedUrl(track.getAudioKey());

        return TrackResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .artist(track.getArtist())
                .duration(track.getDuration())
                .audioUrl(audioUrl)
                .build();
    }
}