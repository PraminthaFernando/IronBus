package com.lsf.ironbus.route.app.response;

import com.lsf.ironbus.route.domain.RouteStation;

import java.math.BigDecimal;
import java.util.UUID;

public record RouteStationResponse(
        UUID id,
        UUID stationId,
        String stationCode,
        String stationName,
        int sequenceNumber,
        BigDecimal distanceFromOriginKm,
        int scheduledOffsetMinutes
) {

    public static RouteStationResponse from(
            RouteStation routeStation
    ) {
        return new RouteStationResponse(
                routeStation.getId(),
                routeStation.getStation().getId(),
                routeStation.getStation().getCode(),
                routeStation.getStation().getName(),
                routeStation.getSequenceNumber(),
                routeStation.getDistanceFromOriginKm(),
                routeStation.getScheduledOffsetMinutes()
        );
    }
}