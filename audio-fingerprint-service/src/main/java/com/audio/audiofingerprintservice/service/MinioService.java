package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.exception.FingerprintException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


@Service
@Slf4j
public class MinioService {
    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public byte[] downloadAudio(String s3Key) {
        validateS3Key(s3Key);

        try {
            return attemptDownload(s3Key);
        } catch (Exception e) {
            log.error("Failed to download audio from MinIO. Key: {}", s3Key, e);
            throw new FingerprintException("Audio download failed", e);
        }
    }

    private void validateS3Key(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new FingerprintException("Invalid S3 key");
        }
    }

    private byte[] attemptDownload(String s3Key) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("audio-bucket")
                        .object(s3Key)
                        .build())) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
}