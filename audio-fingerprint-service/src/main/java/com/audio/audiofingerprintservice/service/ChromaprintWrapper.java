package com.audio.audiofingerprintservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class ChromaprintWrapper {
    private static final String CHROMAPRINT_CLI = "fpcalc";

    public String generateFingerprint(byte[] audioData) throws IOException, InterruptedException {
        log.info("Generating fingerprint for audio data (size: {} bytes)", audioData.length);

        try {
            Path tempFile = Files.createTempFile("audio_", ".tmp");
            try {
                Files.write(tempFile, audioData);
                log.debug("Temp file created: {}", tempFile);

                Process process = new ProcessBuilder()
                        .command(CHROMAPRINT_CLI, tempFile.toString())
                        .redirectErrorStream(true)
                        .start();

                String output = IOUtils.toString(process.getInputStream(), "UTF-8");
                int exitCode = process.waitFor();

                log.debug("fpcalc output:\n{}", output);

                if (exitCode != 0) {
                    throw new IOException("fpcalc exited with code " + exitCode);
                }

                return output.trim();
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (Exception e) {
            log.error("Fingerprint generation failed", e);
            throw e;
        }
    }

}