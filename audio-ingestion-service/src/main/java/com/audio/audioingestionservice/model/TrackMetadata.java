package com.audio.audioingestionservice.model;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Метаданные аудио трека")
public class TrackMetadata {

    @Schema(
            description = "Уникальный идентификатор трека",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String id;

    @Schema(description = "Название трека", example = "Smells Like Teen Spirit")
    private String title;

    @Schema(description = "Исполнитель", example = "Nirvana")
    private String artist;

    @Schema(description = "Длительность в секундах", example = "301")
    private Integer duration;

    @Schema(
            description = "URL аудио файла",
            example = "http://storage.example.com/audio/123.mp3"
    )
    private String audioUrl;
}
