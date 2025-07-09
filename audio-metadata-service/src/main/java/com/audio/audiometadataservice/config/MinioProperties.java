package com.audio.audiometadataservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String internalUrl;
    private String accessKey;
    private String secretKey;
    private String bucket;
}