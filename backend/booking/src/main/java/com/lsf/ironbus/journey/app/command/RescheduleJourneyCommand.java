package com.lsf.ironbus.journey.app.command;

import java.time.Instant;
import java.util.UUID;

public record RescheduleJourneyCommand(
        UUID journeyId,
        UUID trainId,
        UUID routeId,
        Instant departureTime,
        long expectedVersion
) {
}