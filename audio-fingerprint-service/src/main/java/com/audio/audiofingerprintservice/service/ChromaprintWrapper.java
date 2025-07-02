package com.audio.audiofingerprintservice.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ChromaprintWrapper {
    private static final String CHROMAPRINT_CLI = "fpcalc";

    public String generateFingerprint(byte[] audioData) throws IOException, InterruptedException {

        Path tempFile = Files.createTempFile("audio_", ".mp3");
        try {
            Files.write(tempFile, audioData);

            Process process = new ProcessBuilder()
                    .command(CHROMAPRINT_CLI, tempFile.toString())
                    .start();

            String output = IOUtils.toString(process.getInputStream(), "UTF-8");
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("fpcalc failed with exit code " + exitCode);
            }

            String[] parts = output.split("=", 2);
            if (parts.length != 2) {
                throw new IOException("Invalid fpcalc output: " + output);
            }

            return parts[1].trim();
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}