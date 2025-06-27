package com.audio.audiometadataservice.service.impl;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.exception.TrackNotFoundException;
import com.audio.audiometadataservice.mapper.TrackMapper;
import com.audio.audiometadataservice.model.TrackCache;
import com.audio.audiometadataservice.repository.TrackCacheRepository;
import com.audio.audiometadataservice.repository.TrackRepository;
import com.audio.audiometadataservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final TrackCacheRepository trackCacheRepository;
    private final TrackMapper trackMapper;

    @Override
    @Cacheable(value = "tracks", key = "#id")
    public TrackResponse getTrackById(Long id) {
        return trackRepository.findById(id)
                .map(trackMapper::toResponse)
                .orElseThrow(() -> new TrackNotFoundException("Track not found with id: " + id));
    }

    @Override
    @CachePut(value = "tracks", key = "#result.id")
    public TrackResponse createTrack(TrackRequest trackRequest) {
        Track track = trackMapper.toEntity(trackRequest);
        Track saved = trackRepository.save(track);
        trackCacheRepository.save(trackMapper.toCache(saved));
        return trackMapper.toResponse(saved);
    }

    @Override
    @CacheEvict(value = "tracks", key = "#id")
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
        trackCacheRepository.deleteById(id);
    }

    @Override
    public TrackResponse findByAudioKey(String audioKey) {
        Optional<TrackCache> cachedTrack = trackCacheRepository.findByAudioKey(audioKey);
        if (cachedTrack.isPresent()) {
            return trackMapper.toResponse(cachedTrack.get());
        }

        Track track = trackRepository.findByAudioKey(audioKey)
                .orElseThrow(() -> new RuntimeException("Track not found with audioKey: " + audioKey));
        trackCacheRepository.save(trackMapper.toCache(track));
        return trackMapper.toResponse(track);
    }
}