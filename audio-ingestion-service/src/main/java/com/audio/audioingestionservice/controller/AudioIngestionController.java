package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.dto.AudioUploadResponse;
import com.audio.audioingestionservice.service.AudioEventProducer;
import com.audio.audioingestionservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/audio")
@RequiredArgsConstructor
public class AudioIngestionController {
    private final AudioStorageService storageService;
    private final AudioEventProducer eventProducer;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioUploadResponse> uploadAudio(
            @RequestParam("file") MultipartFile file) throws Exception {

        String s3Key = storageService.uploadAudio(file);
        eventProducer.sendAudioUploadEvent(s3Key);

        return ResponseEntity.ok(
                new AudioUploadResponse(s3Key, "Audio uploaded successfully")
        );
    }
}


