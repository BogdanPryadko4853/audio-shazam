package com.audio.audiofingerprintservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
public class AudioFingerprint {
    private String trackId;
    private String title;
    private String artist;
    @Field(type = FieldType.Dense_Vector, dims = 512)
    private List<Float> fingerprint;

    @JsonCreator
    public AudioFingerprint(
            @JsonProperty("trackId") String trackId,
            @JsonProperty("title") String title,
            @JsonProperty("artist") String artist,
            @JsonProperty("fingerprint") List<Float> fingerprint) {
        this.trackId = trackId;
        this.title = title;
        this.artist = artist;
        this.fingerprint = fingerprint;
    }
}
