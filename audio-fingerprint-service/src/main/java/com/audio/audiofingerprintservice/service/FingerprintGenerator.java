package com.audio.audiofingerprintservice.service;

public class FingerprintGenerator {
    private static final int SAMPLE_RATE = 44100;
    private static final int WINDOW_SIZE = 2048;

    public String generate(byte[] audioData) {
        double[] samples = convertToSamples(audioData);

        List<double[]> frames = createFrames(samples);

        List<Peak> peaks = extractPeaks(frames);

        return generateHash(peaks);
    }

    private String generateHash(List<Peak> peaks) {
        return Hashing.sha256()
                .hashString(peaks.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}
