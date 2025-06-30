package com.audio.audiofingerprintservice.repository;

import com.audio.audiofingerprintservice.entity.FingerprintHash;
import org.springframework.data.repository.CrudRepository;

public interface FingerprintHashRepository extends CrudRepository<FingerprintHash, String> {

}

