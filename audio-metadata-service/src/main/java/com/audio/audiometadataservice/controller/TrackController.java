package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<TrackResponse> createTrack(
            @RequestBody @Valid TrackRequest trackRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackService.createTrack(trackRequest));
    }

    @GetMapping("/by-key")
    public ResponseEntity<TrackResponse> getTrackByAudioKey(
            @RequestParam String audioKey) {
        return ResponseEntity.ok(trackService.findByAudioKey(audioKey));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponse> getTrack(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTrackById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
}