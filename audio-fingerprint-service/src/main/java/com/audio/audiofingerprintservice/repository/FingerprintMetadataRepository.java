package com.audio.audiofingerprintservice.repository;

import com.audio.audiofingerprintservice.entity.FingerprintMetadata;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FingerprintMetadataRepository extends JpaRepository<FingerprintMetadata, String> {

}