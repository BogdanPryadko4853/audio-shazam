package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.dto.AudioUploadRequest;
import com.audio.audioingestionservice.dto.AudioUploadResponse;
import com.audio.audioingestionservice.model.TrackMetadata;
import com.audio.audioingestionservice.service.AudioEventProducer;
import com.audio.audioingestionservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/audio")
@RequiredArgsConstructor
public class AudioIngestionController {
    private final AudioStorageService storageService;
    private final AudioEventProducer eventProducer;
    private final MetadataServiceClient metadataClient;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioUploadResponse> uploadAudio(
            @ModelAttribute AudioUploadRequest request) throws Exception {

        String s3Key = storageService.uploadAudio(request.getFile());

        TrackMetadata metadata = metadataClient.createTrack(
                request.getTitle(),
                request.getArtist(),
                request.getAlbum(),
                request.getDuration(),
                s3Key
        );

        eventProducer.sendAudioUploadEvent(
                metadata.getId(),
                s3Key,
                request.getTitle(),
                request.getArtist()
        );

        return ResponseEntity.ok(
                new AudioUploadResponse(s3Key, "Audio uploaded successfully")
        );
    }
}


