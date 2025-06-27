package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.entity.Track;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackCacheRepository extends CrudRepository<Track, Long> {
}