package com.audio.audiometadataservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String internalUrl;
    private String externalUrl;
    private String accessKey;
    private String secretKey;
    private String bucket;
}