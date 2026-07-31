package com.lsf.ironbus.journey.web.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ScheduleJourneyRequest(
        @NotNull
        UUID trainId,

        @NotNull
        UUID routeId,

        @NotNull
        @Future
        Instant departureTime
) {
}