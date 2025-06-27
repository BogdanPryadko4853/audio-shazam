package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.repository.TrackCacheRepository;
import com.audio.audiometadataservice.repository.TrackRepository;
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
public class TrackService {

    private final TrackRepository trackRepository;
    private final TrackCacheRepository trackCacheRepository;

    @Cacheable(value = "tracks", key = "#id")
    public Track getTrackById(Long id){
        log.info("Getting track by id: {}", id);
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + id));
    }

    @CachePut(value = "tracks", key = "#result.id")
    public Track createTrack(Track track) {
        Track saved = trackRepository.save(track);
        trackCacheRepository.save(saved);
        return saved;
    }

    @CacheEvict(value = "tracks", key = "#id")
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
        trackCacheRepository.deleteById(id);
    }

    public Optional<Track> findByAudioKey(String audioKey) {
        return trackCacheRepository.findByAudioKey(audioKey)
                .or(() -> {
                    Optional<Track> dbTrack = trackRepository.findByAudioKey(audioKey);
                    dbTrack.ifPresent(trackCacheRepository::save);
                    return dbTrack;
                });
    }
}
