package com.audio.audiofingerprintservice.dto.response;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Информация о найденном аудио совпадении")
public class AudioMatch {
    @Schema(description = "ID трека", example = "123e4567-e89b-12d3-a456-426614174000")
    private String trackId;

    @Schema(description = "Название трека", example = "Bohemian Rhapsody")
    private String title;

    @Schema(description = "Исполнитель", example = "Queen")
    private String artist;

    @Schema(description = "Длительность в секундах", example = "354")
    private Integer duration;

    @Schema(description = "URL аудио файла", example = "http://storage/audio/123.mp3")
    private String audioUrl;

    @Schema(description = "Уверенность совпадения (0-1)", example = "0.95")
    private Float confidence;
}
