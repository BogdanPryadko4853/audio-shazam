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
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FingerprintService {
    private final ElasticsearchClient esClient;
    private final ChromaprintWrapper chromaprint;
    private final MetadataServiceClient metadataClient;

    private static final String INDEX_NAME = "audio_fingerprints";

    public FingerprintService(ElasticsearchClient esClient,
                              ChromaprintWrapper chromaprint,
                              MetadataServiceClient metadataClient) {
        this.esClient = esClient;
        this.chromaprint = chromaprint;
        this.metadataClient = metadataClient;
    }

    @PostConstruct
    public void init() throws IOException {
        createIndexWithProperMapping();
    }

    private void createIndexWithProperMapping() throws IOException {
        if (!esClient.indices().exists(e -> e.index(INDEX_NAME)).value()) {
            esClient.indices().create(c -> c
                    .index(INDEX_NAME)
                    .mappings(m -> m
                            .properties("trackId", p -> p.keyword(k -> k))
                            .properties("fingerprint", p -> p
                                    .denseVector(d -> d
                                            .dims(2048) // Укажите реальную размерность ваших отпечатков
                                            .index(true)
                                            .similarity("cosine")
                                    )
                            )
                    )
                    .settings(s -> s
                            .index(i -> i
                                    .numberOfShards("1")
                                    .numberOfReplicas("0")
                            )
                    )
            );
            log.info("Created index {} with proper mapping", INDEX_NAME);
        }
    }

    public void saveFingerprint(AudioFingerprint fingerprint) throws IOException {
        try {
            // 1. Проверяем соединение
            if (!checkElasticsearchConnection()) {
                throw new IOException("Elasticsearch is not available");
            }

            // 2. Проверяем/создаём индекс
            if (!indexExists()) {
                createIndexWithProperMapping();
                log.warn("Index {} was recreated", INDEX_NAME);
            }

            // 3. Сохраняем документ
            var response = esClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(fingerprint.getTrackId())
                    .document(fingerprint)
            );

            log.info("Document saved. Index: {}, ID: {}, Version: {}",
                    response.index(),
                    response.id(),
                    response.version());

            // 4. Принудительно обновляем и проверяем
            esClient.indices().refresh(r -> r.index(INDEX_NAME));

            var getResponse = esClient.get(g -> g
                            .index(INDEX_NAME)
                            .id(fingerprint.getTrackId()),
                    AudioFingerprint.class
            );

            if (getResponse.found()) {
                log.info("Verified document in index: {}", getResponse.source());
            } else {
                log.error("Failed to verify saved document!");
            }

        } catch (Exception e) {
            log.error("Failed to save fingerprint for track {}", fingerprint.getTrackId(), e);
            throw e;
        }
    }

    public boolean checkElasticsearchConnection() {
        try {
            return esClient.ping().value();
        } catch (IOException e) {
            log.error("Elasticsearch connection failed", e);
            return false;
        }
    }

    public boolean indexExists() {
        try {
            return esClient.indices().exists(e -> e.index(INDEX_NAME)).value();
        } catch (IOException e) {
            log.error("Index check failed", e);
            return false;
        }
    }


    public  List<Float> generateFingerprint(byte[] audioData) throws FingerprintException {
        try {
            String fpString = chromaprint.generateFingerprint(audioData);
            return parseFingerprintString(fpString);
        } catch (Exception e) {
            throw new FingerprintException("Failed to generate fingerprint", e);
        }
    }

    private List<Float> parseFingerprintString(String fingerprint) {
        // Пример преобразования строки в список чисел
        String[] parts = fingerprint.split(",");
        List<Float> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Float.parseFloat(part.trim()));
        }
        return result;
    }

    public FingerprintMatchResponse searchMatch(MultipartFile audioFile) {
        try {
            // Обновляем индекс перед поиском
            esClient.indices().refresh(r -> r.index(INDEX_NAME));

            byte[] audioData = audioFile.getBytes();
            List<Float> queryFingerprint = generateFingerprint(audioData);

            SearchResponse<AudioFingerprint> response = esClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .scriptScore(ss -> ss
                                            .query(qq -> qq.matchAll(ma -> ma))
                                            .script(s1 -> s1
                                                    .inline(i -> i
                                                            .source("cosineSimilarity(params.query_vector, 'fingerprint') + 1.0")
                                                            .params("query_vector", JsonData.of(queryFingerprint))
                                                    )
                                            )
                                    )
                            )
                            .size(5),
                    AudioFingerprint.class
            );

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
            log.error("Search failed for index {}", INDEX_NAME, e);
            throw new FingerprintException("Search failed", e);
        }
    }
}