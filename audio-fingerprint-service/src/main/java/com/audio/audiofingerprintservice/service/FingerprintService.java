package com.audio.audiofingerprintservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.audio.audiofingerprintservice.dto.AudioMatch;
import com.audio.audiofingerprintservice.dto.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.dto.TrackMetadataResponse;
import com.audio.audiofingerprintservice.exception.FingerprintException;
import com.audio.audiofingerprintservice.model.AudioFingerprint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
            byte[] audioData = audioFile.getBytes();
            List<Float> queryVector = generateFingerprint(audioData);


            List<Double> queryVectorDouble = queryVector.stream()
                    .map(Float::doubleValue)
                    .collect(Collectors.toList());

            SearchResponse<AudioFingerprint> response = esClient.search(s -> s
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
                            )
                            .size(5),
                    AudioFingerprint.class
            );
            return convertToMatchResponse(response);
        } catch (Exception e) {
            throw new FingerprintException("Audio search failed", e);
        }
    }

    private FingerprintMatchResponse convertToMatchResponse(SearchResponse<AudioFingerprint> response) {
        List<AudioMatch> matches = new ArrayList<>();

        for (Hit<AudioFingerprint> hit : response.hits().hits()) {
            if (hit.source() != null) {
                TrackMetadataResponse metadata = metadataServiceClient.getTrackMetadata(hit.source().getTrackId());
                AudioFingerprint fp = hit.source();
                matches.add(AudioMatch.builder()
                        .trackId(fp.getTrackId())
                        .title(metadata.getTitle())
                        .artist(metadata.getArtist())
                        .duration(metadata.getDuration())
                        .confidence(hit.score() != null ? (hit.score().floatValue() - 1.0f) : 0)
                        .build());
            }
        }

        return FingerprintMatchResponse.builder()
                .matches(matches)
                .build();
    }
}