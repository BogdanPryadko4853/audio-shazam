package com.audio.audiofingerprintservice.controller;

import com.audio.audiofingerprintservice.dto.response.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.service.FingerprintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/fingerprints")
@Tag(name = "Fingerprint API", description = "API для работы с аудио отпечатками")
@Slf4j
@RequiredArgsConstructor
public class FingerprintController {

    private final FingerprintService fingerprintService;

    @Operation(
            summary = "Поиск аудио по отпечатку",
            description = "Принимает аудио файл и возвращает найденные совпадения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный поиск",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FingerprintMatchResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат файла"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка обработки аудио"
                    )
            }
    )
    @PostMapping(value = "/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FingerprintMatchResponse searchFingerprint(
            @RequestPart("audio")
            @Schema(description = "Аудио файл в формате MP3 или WAV", format = "binary")
            MultipartFile audioFile) {
        log.info("Received file: {} ({} bytes)", audioFile.getOriginalFilename(), audioFile.getSize());
        return fingerprintService.searchMatch(audioFile);
    }
}