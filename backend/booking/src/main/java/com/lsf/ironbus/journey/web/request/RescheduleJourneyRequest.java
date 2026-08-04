package com.lsf.ironbus.journey.web.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RescheduleJourneyRequest(

        @NotNull(message = "Train ID is required")
        UUID trainId,

        @NotNull(message = "Route ID is required")
        UUID routeId,

        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time must be in the future")
        Instant departureTime,

        @PositiveOrZero(message = "Expected version cannot be negative")
        long expectedVersion

) {
}