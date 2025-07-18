package com.audio.audioingestionservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Запрос на загрузку аудио файла")
public class AudioUploadRequest {

    @Schema(
            description = "Аудио файл (MP3/WAV)",
            format = "binary",
            required = true,
            example = "audio.mp3"
    )
    private MultipartFile file;

    @Schema(
            description = "Название трека",
            required = true,
            example = "Bohemian Rhapsody"
    )
    private String title;

    @Schema(
            description = "Исполнитель",
            required = true,
            example = "Queen"
    )
    private String artist;

    @Schema(
            description = "Длительность в секундах",
            required = true,
            example = "354"
    )
    private Integer duration;
}
