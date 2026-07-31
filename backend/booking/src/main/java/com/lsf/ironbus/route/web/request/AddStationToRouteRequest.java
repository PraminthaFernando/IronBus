package com.lsf.ironbus.route.web.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddStationToRouteRequest(

        @NotNull
        UUID stationId,

        @Min(0)
        int sequenceNumber,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal distanceFromOriginKm,

        @Min(0)
        int scheduledOffsetMinutes
) {
}