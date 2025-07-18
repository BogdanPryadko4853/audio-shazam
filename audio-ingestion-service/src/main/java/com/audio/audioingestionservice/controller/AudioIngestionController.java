package com.audio.audioingestionservice.controller;

import com.audio.audioingestionservice.client.MetadataServiceClient;
import com.audio.audioingestionservice.dto.request.AudioUploadRequest;
import com.audio.audioingestionservice.dto.response.AudioUploadResponse;
import com.audio.audioingestionservice.dto.request.TrackRequest;
import com.audio.audioingestionservice.messaging.producer.AudioEventProducer;
import com.audio.audioingestionservice.model.TrackMetadata;
import com.audio.audioingestionservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/audio")
@RequiredArgsConstructor
@Tag(name = "Audio Ingestion API", description = "API для загрузки и обработки аудио файлов")
public class AudioIngestionController {

    private final AudioStorageService storageService;
    private final MetadataServiceClient metadataClient;
    private final AudioEventProducer eventProducer;

    @Operation(
            summary = "Загрузить аудио файл",
            description = "Загружает аудио файл в хранилище, создает метаданные и отправляет событие обработки",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Аудио успешно загружено",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AudioUploadResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса"
                    ),
                    @ApiResponse(
                            responseCode = "413",
                            description = "Размер файла превышает допустимый лимит"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка при обработке файла"
                    )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioUploadResponse> uploadAudio(
            @ModelAttribute
            @Schema(description = "Данные для загрузки аудио")
            AudioUploadRequest request) throws Exception {

        String s3Key = storageService.uploadAudio(request.getFile());

        TrackMetadata metadata = metadataClient.createTrack(
                TrackRequest.builder()
                        .title(request.getTitle())
                        .artist(request.getArtist())
                        .duration(request.getDuration())
                        .audioKey(s3Key)
                        .build()
        );

        eventProducer.sendAudioUploadEvent(
                metadata.getId(),
                s3Key
        );

        return ResponseEntity.ok(
                new AudioUploadResponse(s3Key, "Audio uploaded successfully")
        );
    }
}


