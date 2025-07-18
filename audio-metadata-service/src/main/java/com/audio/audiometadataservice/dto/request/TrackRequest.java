package com.audio.audiometadataservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Schema(description = "Запрос для создания/обновления метаданных трека")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackRequest {

    @NotBlank
    @Schema(description = "Название трека", required = true, example = "Bohemian Rhapsody")
    private String title;

    @NotBlank
    @Schema(description = "Исполнитель", required = true, example = "Queen")
    private String artist;

    @Schema(description = "Альбом", example = "A Night at the Opera")
    private String album;

    @Schema(description = "Длительность в секундах", example = "354")
    private Integer duration;

    @NotBlank
    @Schema(
            description = "Ключ аудио файла в хранилище",
            required = true,
            example = "audio/123e4567-e89b-12d3-a456-426614174000.mp3"
    )
    private String audioKey;
}