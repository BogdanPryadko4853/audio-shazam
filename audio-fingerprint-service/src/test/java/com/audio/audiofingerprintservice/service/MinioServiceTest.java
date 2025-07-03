package com.audio.audiofingerprintservice.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @Test
    void downloadAudio_ShouldReturnAudioData() throws Exception {
        // Arrange
        byte[] testData = "test data".getBytes();
        InputStream mockStream = new ByteArrayInputStream(testData);
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(mockResponse);
        when(mockResponse.read(any(byte[].class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    byte[] buf = invocation.getArgument(0);
                    System.arraycopy(testData, 0, buf, 0, testData.length);
                    return testData.length;
                })
                .thenReturn(-1); // End of stream

        // Act
        byte[] result = minioService.downloadAudio("test.mp3");

        // Assert
        assertNotNull(result);
        assertArrayEquals(testData, result);
        verify(mockResponse).close();
    }

    @Test
    void downloadAudio_ShouldHandleEmptyStream() throws Exception {
        // Arrange
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(mockResponse);
        when(mockResponse.read(any(byte[].class), anyInt(), anyInt()))
                .thenReturn(-1); // End of stream

        // Act
        byte[] result = minioService.downloadAudio("empty.mp3");

        // Assert
        assertEquals(0, result.length);
    }
}