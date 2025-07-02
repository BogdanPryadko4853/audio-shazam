package com.audio.audioingestionservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AudioUploadRequest {
    private MultipartFile file;
    private String title;
    private String artist;
    private Integer duration;
}
