package com.audio.audiofingerprintservice.client;

import com.audio.audiofingerprintservice.dto.response.TrackMetadataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetadataServiceClient {
    private final RestTemplate restTemplate;

    @Value("${metadata.service.url}")
    private String metadataServiceUrl;

    public MetadataServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TrackMetadataResponse getTrackMetadata(String trackId) {
        String url = metadataServiceUrl + "/api/v1/tracks/" + trackId;
        return restTemplate.getForObject(url, TrackMetadataResponse.class);
    }
}