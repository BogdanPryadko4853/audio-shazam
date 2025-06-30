package com.audio.audiofingerprintservice.service;

import com.audio.audiofingerprintservice.config.AppConfig;
import com.audio.audiofingerprintservice.model.Peak;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.TransformType;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FingerprintGenerator {
    private final AppConfig appConfig;

    public String generate(byte[] audioData) {
        try {

            double[] samples = convertToSamples(audioData);

            List<double[]> frames = createFrames(samples);

            List<Peak> peaks = extractPeaks(frames);

            return generateHash(peaks);
        } catch (Exception e) {
            log.error("Error generating fingerprint", e);
            throw new RuntimeException(e);
        }
    }

    private double[] convertToSamples(byte[] audioBytes) {
        double[] samples = new double[audioBytes.length / 2];
        for (int i = 0; i < samples.length; i++) {
            short sample = (short) ((audioBytes[2 * i + 1] << 8) | (audioBytes[2 * i] & 0xFF));
            samples[i] = sample / 32768.0;
        }
        return samples;
    }

    private List<double[]> createFrames(double[] samples) {
        List<double[]> frames = new ArrayList<>();
        int windowSize = appConfig.getWindowSize();

        for (int i = 0; i <= samples.length - windowSize; i += windowSize / 2) {
            double[] frame = Arrays.copyOfRange(samples, i, i + windowSize);
            applyHammingWindow(frame);
            frames.add(frame);
        }

        return frames;
    }

    private void applyHammingWindow(double[] frame) {
        for (int i = 0; i < frame.length; i++) {
            frame[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (frame.length - 1));
        }
    }

    private List<Peak> extractPeaks(List<double[]> frames) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        List<Peak> peaks = new ArrayList<>();
        double minAmplitude = appConfig.getMinAmplitude();

        for (int frameIndex = 0; frameIndex < frames.size(); frameIndex++) {
            double[] frame = frames.get(frameIndex);
            Complex[] spectrum = fft.transform(frame, TransformType.FORWARD);

            for (int bin = 0; bin < spectrum.length / 2; bin++) {
                double amplitude = spectrum[bin].abs();
                if (amplitude > minAmplitude) {
                    double freq = bin * appConfig.getSampleRate() / frame.length;
                    double time = frameIndex * (frame.length / 2) / (double) appConfig.getSampleRate();
                    peaks.add(new Peak(freq, amplitude, time));
                }
            }
        }

        return peaks;
    }

    private String generateHash(List<Peak> peaks) {
        String peaksString = peaks.stream()
                .map(p -> String.format("%.1f:%.1f", p.getFrequency(), p.getTimeOffset()))
                .collect(Collectors.joining(","));

        return Hashing.sha256()
                .hashString(peaksString, StandardCharsets.UTF_8)
                .toString();
    }
}