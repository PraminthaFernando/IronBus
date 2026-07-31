package com.lsf.ironbus.journey.app.response;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;

import java.time.Instant;
import java.util.UUID;

public record JourneyResponse(
        UUID id,
        UUID trainId,
        String trainCode,
        UUID routeId,
        String routeCode,
        Instant departureTime,
        JourneyStatus status
) {
    public static JourneyResponse from(Journey journey) {
        return new JourneyResponse(
                journey.getId(),
                journey.getTrain().getId(),
                journey.getTrain().getCode(),
                journey.getRoute().getId(),
                journey.getRoute().getCode(),
                journey.getDepartureTime(),
                journey.getStatus()
        );
    }
}