package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.model.TrackMetadata;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "metadata-service", url = "${metadata.service.url}")
public interface MetadataServiceClient {

    @PostMapping("/tracks")
    TrackMetadata createTrack(
            @RequestParam String title,
            @RequestParam String artist,
            @RequestParam String album,
            @RequestParam Integer duration,
            @RequestParam String audioPath
    );
}
