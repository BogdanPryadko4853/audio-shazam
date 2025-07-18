package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.exception.FingerprintException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SimpleFingerprintService {

    private static final int FINGERPRINT_SIZE = 512;


    private float[] convertBytesToSamples(byte[] bytes) {
        ShortBuffer shortBuffer = ByteBuffer.wrap(bytes).asShortBuffer();
        float[] samples = new float[shortBuffer.remaining()];

        for (int i = 0; i < samples.length; i++) {
            samples[i] = shortBuffer.get() / 32768.0f;
        }
        return samples;
    }

    private void normalizeSamples(float[] samples) {
        float max = 0.01f;
        for (float sample : samples) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }

        for (int i = 0; i < samples.length; i++) {
            samples[i] /= max;
        }
    }

    private List<Float> extractPeaks(float[] samples) {
        List<Float> fingerprint = new ArrayList<>();
        int step = Math.max(1, samples.length / FINGERPRINT_SIZE);

        for (int i = 0; i < FINGERPRINT_SIZE && i * step < samples.length; i++) {
            int start = i * step;
            int end = Math.min(start + step, samples.length);

            float max = 0;
            for (int j = start; j < end; j++) {
                max = Math.max(max, Math.abs(samples[j]));
            }
            fingerprint.add(max);
        }

        while (fingerprint.size() < FINGERPRINT_SIZE) {
            fingerprint.add(0f);
        }

        return fingerprint;
    }

    public List<Float> generateFingerprint(byte[] audioData) throws FingerprintException {
        try {
            log.debug("Input audio size: {} bytes", audioData.length);

            if (isMp3(audioData)) {
                log.debug("Detected MP3, converting to PCM...");
                audioData = convertMp3ToPcm(audioData);
                log.debug("PCM audio size after conversion: {} bytes", audioData.length);
            }

            float[] samples = convertBytesToSamples(audioData);
            normalizeSamples(samples);
            return extractPeaks(samples);
        } catch (Exception e) {
            log.error("Failed to process audio", e);
            throw new FingerprintException("Fingerprint generation failed", e);
        }
    }

    private boolean isMp3(byte[] data) {
        return data.length > 2 && (data[0] == (byte) 0xFF && data[1] == (byte) 0xFB)
                || (data[0] == 'I' && data[1] == 'D' && data[2] == '3');
    }

    private byte[] convertMp3ToPcm(byte[] mp3Data) throws UnsupportedAudioFileException, IOException {
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(mp3Data)
        );

        AudioFormat pcmFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100,      // Sample rate
                16,         // Sample size in bits
                1,          // Channels (mono)
                2,          // Frame size (bytes)
                44100,      // Frame rate
                false       // Big-endian
        );

        AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, mp3Stream);
        ByteArrayOutputStream pcmOut = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = pcmStream.read(buffer)) != -1) {
            pcmOut.write(buffer, 0, bytesRead);
        }

        return pcmOut.toByteArray();
    }
}