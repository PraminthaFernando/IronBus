package com.lsf.ironbus.route.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ReplaceRouteStationsRequest(

        @NotEmpty
        List<@Valid RouteStationItem> stations,

        @NotNull
        @Min(0)
        Long expectedVersion
) {

    public record RouteStationItem(

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
}