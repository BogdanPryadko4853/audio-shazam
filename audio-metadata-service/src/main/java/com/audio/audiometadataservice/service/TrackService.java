package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.dto.request.TrackRequest;
import com.audio.audiometadataservice.dto.response.TrackResponse;

public interface TrackService {
    TrackResponse getTrackById(Long id);
    TrackResponse createTrack(TrackRequest trackRequest);
    void deleteTrack(Long id);
    TrackResponse findByAudioKey(String audioKey);
}
