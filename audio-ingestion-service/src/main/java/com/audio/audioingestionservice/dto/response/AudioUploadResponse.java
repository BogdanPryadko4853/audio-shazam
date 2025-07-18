package com.audio.audioingestionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Ответ после успешной загрузки аудио")
@AllArgsConstructor
@NoArgsConstructor
public class AudioUploadResponse {

    @Schema(
            description = "Ключ файла в хранилище",
            example = "audio/123e4567-e89b-12d3-a456-426614174000.mp3"
    )
    private String storageKey;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Audio uploaded successfully"
    )
    private String message;


}