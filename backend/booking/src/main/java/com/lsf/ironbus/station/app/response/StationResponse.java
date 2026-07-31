package com.lsf.ironbus.station.app.response;

import com.lsf.ironbus.station.domain.Station;

import java.time.Instant;
import java.util.UUID;

public record StationResponse(
        UUID id,
        String code,
        String name,
        boolean active,
        Instant createdAt,
        long version
) {

    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getCode(),
                station.getName(),
                station.isActive(),
                station.getCreatedAt(),
                station.getVersion()
        );
    }
}