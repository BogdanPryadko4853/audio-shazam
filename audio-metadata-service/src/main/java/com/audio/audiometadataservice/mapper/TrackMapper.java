package com.audio.audiometadataservice.mapper;

import com.audio.audiometadataservice.dto.TrackRequest;
import com.audio.audiometadataservice.dto.TrackResponse;
import com.audio.audiometadataservice.entity.Track;
import com.audio.audiometadataservice.model.TrackCache;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TrackMapper {
    TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

    Track toEntity(TrackRequest trackRequest);

    TrackResponse toResponse(Track track);
    TrackResponse toResponse(TrackCache trackCache);

    TrackCache toCache(Track track);
}
