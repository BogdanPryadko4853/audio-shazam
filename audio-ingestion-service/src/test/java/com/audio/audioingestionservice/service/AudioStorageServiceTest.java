package com.audio.audioingestionservice.service;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AudioStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private AudioStorageService storageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "bucket", "test-bucket");
    }

    @Test
    void shouldUploadAudio() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test.mp3", "test.mp3", "audio/mpeg", "test-data".getBytes());

        when(minioClient.bucketExists(any())).thenReturn(true);

        // Act
        String result = storageService.uploadAudio(file);

        // Assert
        assertThat(result).contains("test.mp3");
        verify(minioClient).putObject(any());
    }
}