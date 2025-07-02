package com.audio.audiometadataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class AudioMetadataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioMetadataServiceApplication.class, args);
    }

}
