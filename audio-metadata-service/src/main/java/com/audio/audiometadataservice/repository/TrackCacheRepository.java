package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.model.TrackCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackCacheRepository extends CrudRepository<TrackCache, Long> {
    Optional<TrackCache> findByAudioKey(String audioKey);
}