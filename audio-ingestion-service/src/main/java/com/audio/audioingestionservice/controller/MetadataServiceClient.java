package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.dto.TrackRequest;
import com.audio.audioingestionservice.model.TrackMetadata;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "metadata-service", url = "${metadata.service.url}")
public interface MetadataServiceClient {

    @PostMapping("/tracks")
    TrackMetadata createTrack(
            @RequestBody TrackRequest trackRequest
    );
}
