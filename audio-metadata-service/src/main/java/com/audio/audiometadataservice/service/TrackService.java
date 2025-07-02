package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;

public interface TrackService {
    TrackResponse getTrackById(Long id);
    TrackResponse createTrack(TrackRequest trackRequest);
    void deleteTrack(Long id);
    TrackResponse findByAudioKey(String audioKey); // Изменено имя метода
}
