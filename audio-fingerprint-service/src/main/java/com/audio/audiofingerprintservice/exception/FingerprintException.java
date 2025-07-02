package com.audio.audiofingerprintservice.exception;

public class FingerprintException extends RuntimeException {
    public FingerprintException(String message) {
        super(message);
    }

    public FingerprintException(String message, Throwable cause) {
        super(message, cause);
    }
}