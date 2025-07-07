package com.audio.audiofingerprintservice.controller;

import com.audio.audiofingerprintservice.dto.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.service.FingerprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/fingerprints")
@Slf4j
public class FingerprintController {
    private final FingerprintService fingerprintService;

    public FingerprintController(FingerprintService fingerprintService) {
        this.fingerprintService = fingerprintService;
    }

    @PostMapping(value = "/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FingerprintMatchResponse searchFingerprint(@RequestPart("audio") MultipartFile audioFile) {
        log.info("Received file: {} ({} bytes)", audioFile.getOriginalFilename(), audioFile.getSize());
        return fingerprintService.searchMatch(audioFile);
    }
}