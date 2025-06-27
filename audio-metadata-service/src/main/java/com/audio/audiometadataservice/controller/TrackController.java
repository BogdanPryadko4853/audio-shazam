package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.entity.Track;
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

    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrack(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTrackById(id));
    }

    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody @Valid Track track) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackService.createTrack(track));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
}
