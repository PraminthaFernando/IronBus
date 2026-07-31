package com.lsf.ironbus.route.app.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AddStationToRouteCommand(
        UUID routeId,
        UUID stationId,
        int sequenceNumber,
        BigDecimal distanceFromOriginKm,
        int scheduledOffsetMinutes
) {
}