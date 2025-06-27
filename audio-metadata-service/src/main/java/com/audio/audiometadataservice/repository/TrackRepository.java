package com.audio.audiometadataservice.repository;

import com.audio.audiometadataservice.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByAudioKey(String audioKey);
}