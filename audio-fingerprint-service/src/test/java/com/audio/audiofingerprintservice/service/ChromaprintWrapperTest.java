package com.audio.audiofingerprintservice.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChromaprintWrapperTest {

    @InjectMocks
    private ChromaprintWrapper chromaprintWrapper;

    @Test
    void generateFingerprint_ShouldReturnFingerprint() throws Exception {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            // Mock temp file creation
            Path mockPath = mock(Path.class);
            when(mockPath.toAbsolutePath()).thenReturn(mockPath);
            when(mockPath.toString()).thenReturn("/tmp/audio_temp");

            filesMock.when(() -> Files.createTempFile(anyString(), anyString(), any()))
                    .thenReturn(mockPath);

            // Mock file write
            filesMock.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(mockPath);

            // Mock process execution
            Process mockProcess = mock(Process.class);
            when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("FINGERPRINT=AQADtErl".getBytes()));
            when(mockProcess.waitFor()).thenReturn(0);

            // Mock ProcessBuilder
            try (MockedConstruction<ProcessBuilder> ignored = mockConstruction(ProcessBuilder.class,
                    (mock, context) -> {
                        when(mock.command(any(String[].class))).thenReturn(mock);
                        when(mock.start()).thenReturn(mockProcess);
                    })) {

                String result = chromaprintWrapper.generateFingerprint("test".getBytes());
                assertEquals("AQADtErl", result);

                // Verify temp file was deleted
                filesMock.verify(() -> Files.deleteIfExists(mockPath));
            }
        }
    }

    @Test
    void generateFingerprint_ShouldThrowException_WhenProcessFails() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path mockPath = mock(Path.class);
            when(mockPath.toAbsolutePath()).thenReturn(mockPath);
            when(mockPath.toString()).thenReturn("/tmp/audio_temp");

            filesMock.when(() -> Files.createTempFile(anyString(), anyString(), any()))
                    .thenReturn(mockPath);

            Process mockProcess = mock(Process.class);
            when(mockProcess.waitFor()).thenReturn(1);
            when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream("Error".getBytes()));

            try (MockedConstruction<ProcessBuilder> ignored = mockConstruction(ProcessBuilder.class,
                    (mock, context) -> {
                        when(mock.command(any(String[].class))).thenReturn(mock);
                        when(mock.start()).thenReturn(mockProcess);
                    })) {

                IOException exception = assertThrows(IOException.class, () -> {
                    chromaprintWrapper.generateFingerprint("test".getBytes());
                });
                assertTrue(exception.getMessage().contains("fpcalc failed"));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateFingerprint_ShouldThrowException_WhenInvalidOutput() throws InterruptedException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path mockPath = mock(Path.class);
            when(mockPath.toAbsolutePath()).thenReturn(mockPath);
            when(mockPath.toString()).thenReturn("/tmp/audio_temp");

            filesMock.when(() -> Files.createTempFile(anyString(), anyString(), any()))
                    .thenReturn(mockPath);

            Process mockProcess = mock(Process.class);
            when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("INVALID_OUTPUT".getBytes()));
            when(mockProcess.waitFor()).thenReturn(0);

            try (MockedConstruction<ProcessBuilder> ignored = mockConstruction(ProcessBuilder.class,
                    (mock, context) -> {
                        when(mock.command(any(String[].class))).thenReturn(mock);
                        when(mock.start()).thenReturn(mockProcess);
                    })) {

                IOException exception = assertThrows(IOException.class, () -> {
                    chromaprintWrapper.generateFingerprint("test".getBytes());
                });
                assertTrue(exception.getMessage().contains("Invalid fpcalc output"));
            }
        }
    }

    @Test
    void generateFingerprint_ShouldThrowException_WhenNullInput() {
        assertThrows(IOException.class, () -> {
            chromaprintWrapper.generateFingerprint(null);
        });
    }
}