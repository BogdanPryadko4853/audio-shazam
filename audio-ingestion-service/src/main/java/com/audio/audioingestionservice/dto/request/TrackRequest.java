package com.audio.audioingestionservice.dto.request;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Запрос для создания метаданных трека")
@Builder
public class TrackRequest {

    @Schema(description = "Название трека", example = "Yesterday")
    private String title;

    @Schema(description = "Исполнитель", example = "The Beatles")
    private String artist;

    @Schema(description = "Длительность в секундах", example = "125")
    private Integer duration;

    @Schema(
            description = "Ключ аудио файла в хранилище",
            example = "audio/123e4567-e89b-12d3-a456-426614174000.mp3"
    )
    private String audioKey;
}

