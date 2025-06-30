package com.audio.audiofingerprintservice.repository;

import com.audio.audiofingerprintservice.model.entity.FingerprintHash;
import org.springframework.data.repository.CrudRepository;

public interface FingerprintHashRepository extends CrudRepository<FingerprintHash, String> {

}

