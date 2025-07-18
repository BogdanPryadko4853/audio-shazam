package com.audio.audiometadataservice.dto.response;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Ответ с метаданными трека")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackResponse {

    @Schema(description = "Уникальный идентификатор трека", example = "123")
    private Long id;

    @Schema(description = "Название трека", example = "Smells Like Teen Spirit")
    private String title;

    @Schema(description = "Исполнитель", example = "Nirvana")
    private String artist;

    @Schema(
            description = "URL аудио файла",
            example = "http://storage.example.com/audio/123.mp3"
    )
    private String audioUrl;

    @Schema(description = "Длительность в секундах", example = "301")
    private Integer duration;
}