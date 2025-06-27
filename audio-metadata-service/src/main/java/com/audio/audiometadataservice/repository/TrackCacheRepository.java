package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.model.TrackCache;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TrackCacheRepository extends CrudRepository<TrackCache, Long> {
    Optional<TrackCache> findByAudioKey(String audioKey);
}