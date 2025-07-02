package com.audio.audioingestionservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AudioUploadRequest {
    private MultipartFile file;
    private String title;
    private String artist;
    private Integer duration;
}
