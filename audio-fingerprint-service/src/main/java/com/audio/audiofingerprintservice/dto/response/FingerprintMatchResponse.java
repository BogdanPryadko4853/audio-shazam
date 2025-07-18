package com.audio.audiofingerprintservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Результат поиска по аудио отпечатку")
public class FingerprintMatchResponse {
    @Schema(description = "Список найденных совпадений")
    private List<AudioMatch> matches;
}
