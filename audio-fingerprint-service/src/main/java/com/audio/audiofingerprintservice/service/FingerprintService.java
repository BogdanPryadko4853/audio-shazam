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
    private static final int FINGERPRINT_SIZE = 512;

    public List<Float> generateFingerprint(byte[] audioData) throws FingerprintException {
        return simpleFingerprintService.generateFingerprint(audioData);
    }

    public void saveFingerprint(AudioFingerprint fingerprint) throws IOException {
        // Убедимся, что размер fingerprint правильный
        if (fingerprint.getFingerprint().size() != FINGERPRINT_SIZE) {
            throw new FingerprintException("Fingerprint must have exactly " + FINGERPRINT_SIZE + " elements");
        }

        IndexResponse response = esClient.index(i -> i
                .index(INDEX_NAME)
                .id(fingerprint.getTrackId())
                .document(fingerprint)
        );
        esClient.indices().refresh(r -> r.index(INDEX_NAME));
        log.info("Saved fingerprint for track {} with version {}", fingerprint.getTrackId(), response.version());
    }

    public FingerprintMatchResponse searchMatch(MultipartFile audioFile) {
        log.info("Starting search for audio file: {}", audioFile.getOriginalFilename());
        try {
            validateAudioFile(audioFile);
            byte[] audioData = audioFile.getBytes();
            log.debug("Audio data size: {} bytes", audioData.length);

            List<Float> queryVector = generateFingerprint(audioData);
            log.debug("Generated fingerprint vector size: {}", queryVector.size());

            SearchResponse<AudioFingerprint> response = executeElasticsearchQuery(queryVector);
            log.info("Found {} matches", response.hits().hits().size());

            return convertToBestMatchResponse(response);
        } catch (Exception e) {
            log.error("Search failed for file: {}", audioFile.getOriginalFilename(), e);
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
            float[] queryVectorArray = new float[queryVector.size()];
            for (int i = 0; i < queryVector.size(); i++) {
                queryVectorArray[i] = queryVector.get(i);
            }

            return esClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .scriptScore(ss -> ss
                                            .query(qb -> qb.matchAll(m -> m))
                                            .script(sc -> sc
                                                    .inline(i -> i
                                                            .source("cosineSimilarity(params.query_vector, 'fingerprint') + 1.0")
                                                            .params("query_vector", JsonData.of(queryVectorArray))
                                                    )
                                            )
                                            .minScore(1.5f)
                                    )
                            ).size(5),
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
