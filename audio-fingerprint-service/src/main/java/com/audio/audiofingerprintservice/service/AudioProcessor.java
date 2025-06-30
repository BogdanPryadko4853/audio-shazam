package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AudioProcessor {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public byte[] downloadAudio(String s3Path) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(s3Path)
                        .build())) {
            return stream.readAllBytes();
        }
    }
}
