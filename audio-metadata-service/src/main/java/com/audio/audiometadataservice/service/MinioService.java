package com.audio.audiometadataservice.service;

import com.audio.audiometadataservice.config.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient externalMinioClient;
    private final MinioProperties properties;

    public String generatePresignedUrl(String objectPath) {
        try {
            return externalMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucket())
                            .object(objectPath)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}

