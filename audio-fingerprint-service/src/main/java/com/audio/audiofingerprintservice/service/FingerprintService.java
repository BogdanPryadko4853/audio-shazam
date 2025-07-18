package com.audio.audiofingerprintservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.audio.audiofingerprintservice.client.MetadataServiceClient;
import com.audio.audiofingerprintservice.dto.response.AudioMatch;
import com.audio.audiofingerprintservice.dto.response.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.dto.response.TrackMetadataResponse;
import com.audio.audiofingerprintservice.exception.FingerprintException;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FingerprintService {

    private final ElasticsearchClient esClient;
    private final SimpleFingerprintService simpleFingerprintService;
    private final MetadataServiceClient metadataServiceClient;

    private static final String INDEX_NAME = "audio_fingerprints";

    public List<Float> generateFingerprint(byte[] audioData) throws FingerprintException {
        return simpleFingerprintService.generateFingerprint(audioData);
    }

    public void saveFingerprint(AudioFingerprint fingerprint) throws IOException {
        IndexResponse response = esClient.index(i -> i
                .index(INDEX_NAME)
                .id(fingerprint.getTrackId())
                .document(fingerprint)
        );
        esClient.indices().refresh(r -> r.index(INDEX_NAME));
    }

    public FingerprintMatchResponse searchMatch(MultipartFile audioFile) {
        try {
            validateAudioFile(audioFile);
            byte[] audioData = audioFile.getBytes();
            List<Float> queryVector = generateFingerprint(audioData);

            SearchResponse<AudioFingerprint> response = executeElasticsearchQuery(queryVector);
            return convertToBestMatchResponse(response);

        } catch (FingerprintException e) {
            log.error("Fingerprint processing error", e);
            throw e;
        } catch (IOException e) {
            log.error("File reading error", e);
            throw new FingerprintException("Failed to read audio file", e);
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            throw new FingerprintException("Audio search failed", e);
        }
    }

    private void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FingerprintException("Audio file is empty");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB max
            throw new FingerprintException("File size exceeds maximum limit");
        }
    }

    private SearchResponse<AudioFingerprint> executeElasticsearchQuery(List<Float> queryVector) {
        try {
            List<Double> queryVectorDouble = queryVector.stream()
                    .map(Float::doubleValue)
                    .collect(Collectors.toList());

            return esClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .scriptScore(ss -> ss
                                            .query(qb -> qb.matchAll(m -> m))
                                            .script(sc -> sc
                                                    .inline(i -> i
                                                            .source("cosineSimilarity(params.query_vector, 'fingerprint') + 1.0")
                                                            .params("query_vector", JsonData.of(queryVectorDouble))
                                                    )
                                            )
                                            .minScore(1.2f)
                                    )
                            ),
                    AudioFingerprint.class);
        } catch (IOException e) {
            log.error("Elasticsearch query failed", e);
            throw new FingerprintException("Search service unavailable", e);
        }
    }


    private FingerprintMatchResponse convertToBestMatchResponse(SearchResponse<AudioFingerprint> response) {
        if (response.hits().hits().isEmpty()) {
            return FingerprintMatchResponse.builder()
                    .matches(List.of())
                    .build();
        }

        Hit<AudioFingerprint> bestHit = response.hits().hits().get(0);
        if (bestHit.source() == null) {
            return FingerprintMatchResponse.builder()
                    .matches(List.of())
                    .build();
        }

        AudioFingerprint fp = bestHit.source();
        TrackMetadataResponse metadata = metadataServiceClient.getTrackMetadata(fp.getTrackId());

        AudioMatch bestMatch = AudioMatch.builder()
                .trackId(fp.getTrackId())
                .title(metadata.getTitle())
                .artist(metadata.getArtist())
                .duration(metadata.getDuration())
                .audioUrl(metadata.getAudioUrl())
                .confidence(bestHit.score() != null ? (bestHit.score().floatValue() - 1.0f) : 0)
                .build();

        return FingerprintMatchResponse.builder()
                .matches(List.of(bestMatch))
                .build();
    }
}
