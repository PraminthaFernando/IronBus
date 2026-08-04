package com.lsf.ironbus.journey.app.response;

import com.lsf.ironbus.journey.enums.JourneyStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record JourneyAdminResponse(
        UUID id,
        UUID trainId,
        String trainCode,
        UUID routeId,
        String routeCode,
        Instant departureTime,
        JourneyStatus status,
        long bookingCount,
        BigDecimal occupancyPercentage,
        long version
) {
}