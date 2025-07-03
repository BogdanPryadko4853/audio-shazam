package com.audio.audiofingerprintservice;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.audio.audiofingerprintservice.model.AudioFingerprint;

import java.util.Collections;

public class TestUtils {

    public static SearchResponse<AudioFingerprint> createMockSearchResponse() {
        AudioFingerprint fingerprint = new AudioFingerprint();
        fingerprint.setTrackId("123");
        fingerprint.setFingerprint("test_fp");

        Hit<AudioFingerprint> hit = new Hit.Builder<AudioFingerprint>()
                .id("1")
                .score(1.8)
                .source(fingerprint)
                .build();

        HitsMetadata<AudioFingerprint> hits = new HitsMetadata.Builder<AudioFingerprint>()
                .hits(Collections.singletonList(hit))
                .total(t -> t.value(1L))
                .build();

        return new SearchResponse.Builder<AudioFingerprint>()
                .hits(hits)
                .took(10L)
                .build();
    }
}