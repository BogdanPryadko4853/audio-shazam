package com.audio.audiometadataservice.controller;

import com.audio.audiometadataservice.dto.request.TrackRequest;
import com.audio.audiometadataservice.dto.response.TrackResponse;
import com.audio.audiometadataservice.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
@Tag(name = "Track Metadata API", description = "API для управления метаданными аудио треков")
public class TrackController {

    private final TrackService trackService;

    @Operation(
            summary = "Создать новый трек",
            description = "Создает запись метаданных для аудио трека",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Трек успешно создан",
                            content = @Content(
                                    schema = @Schema(implementation = TrackResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные запроса"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TrackResponse> createTrack(
            @RequestBody @Valid
            @Schema(description = "Данные для создания трека", required = true)
            TrackRequest trackRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackService.createTrack(trackRequest));
    }

    @Operation(
            summary = "Получить трек по ключу аудио",
            description = "Возвращает метаданные трека по ключу аудио файла",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Метаданные трека",
                            content = @Content(
                                    schema = @Schema(implementation = TrackResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Трек не найден"
                    )
            }
    )
    @GetMapping("/by-key")
    public ResponseEntity<TrackResponse> getTrackByAudioKey(
            @Parameter(description = "Ключ аудио файла в хранилище", required = true, example = "audio/123.mp3")
            @RequestParam String audioKey) {
        return ResponseEntity.ok(trackService.findByAudioKey(audioKey));
    }

    @Operation(
            summary = "Получить трек по ID",
            description = "Возвращает метаданные трека по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Метаданные трека",
                            content = @Content(
                                    schema = @Schema(implementation = TrackResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Трек не найден"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TrackResponse> getTrack(
            @Parameter(description = "ID трека", required = true, example = "123")
            @PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTrackById(id));
    }

    @Operation(
            summary = "Удалить трек",
            description = "Удаляет метаданные трека по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Трек успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Трек не найден"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(
            @Parameter(description = "ID трека для удаления", required = true, example = "123")
            @PathVariable Long id) {
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
}