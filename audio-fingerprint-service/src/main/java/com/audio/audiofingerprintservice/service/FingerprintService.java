package com.audio.audiofingerprintservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.audio.audiofingerprintservice.dto.AudioMatch;
import com.audio.audiofingerprintservice.dto.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import com.audio.audiofingerprintservice.exception.FingerprintException;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FingerprintService {
    private final ElasticsearchClient esClient;
    private final ChromaprintWrapper chromaprint;
    private final MetadataServiceClient metadataClient;

    public FingerprintService(ElasticsearchClient esClient,
                              ChromaprintWrapper chromaprint,
                              MetadataServiceClient metadataClient) {
        this.esClient = esClient;
        this.chromaprint = chromaprint;
        this.metadataClient = metadataClient;
    }

    public String generateFingerprint(byte[] audioData) throws FingerprintException {
        try {
            return chromaprint.generateFingerprint(audioData);
        } catch (Exception e) {
            throw new FingerprintException("Failed to generate fingerprint", e);
        }
    }

    public void saveFingerprint(AudioFingerprint fingerprint) throws IOException {
        esClient.index(i -> i
                .index("audio_fingerprints")
                .id(fingerprint.getTrackId())
                .document(fingerprint)
        );
    }

    public FingerprintMatchResponse searchMatch(MultipartFile audioFile) {
        try {
            byte[] audioData = audioFile.getBytes();
            String queryFingerprint = generateFingerprint(audioData);

            Script script = Script.of(s -> s
                    .inline(i -> i
                            .source("cosineSimilarity(params.query_vector, 'fingerprint') + 1.0")
                            .params("query_vector", JsonData.of(queryFingerprint))
                    )
            );

            SearchRequest request = SearchRequest.of(s -> s
                    .index("audio_fingerprints")
                    .query(q -> q
                            .scriptScore(ss -> ss
                                    .query(qq -> qq.matchAll(ma -> ma))
                                    .script(script)
                            )
                    )
                    .size(5)
            );

            SearchResponse<AudioFingerprint> response = esClient.search(request, AudioFingerprint.class);

            List<AudioMatch> matches = new ArrayList<>();
            for (Hit<AudioFingerprint> hit : response.hits().hits()) {
                AudioFingerprint fingerprint = hit.source();
                if (fingerprint != null) {
                    TrackMetadataResponse metadata = metadataClient.getTrackMetadata(fingerprint.getTrackId());

                    AudioMatch match = new AudioMatch();
                    match.setTrackId(fingerprint.getTrackId());
                    match.setTitle(metadata.getTitle());
                    match.setArtist(metadata.getArtist());
                    match.setDuration(metadata.getDuration());
                    match.setAudioUrl(metadata.getAudioUrl());
                    match.setConfidence(hit.score().floatValue() / 2);

                    matches.add(match);
                }
            }

            FingerprintMatchResponse result = new FingerprintMatchResponse();
            result.setMatches(matches);
            return result;

        } catch (Exception e) {
            throw new FingerprintException("Search failed", e);
        }
    }
}