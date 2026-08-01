package com.lsf.ironbus.journey.app.command;

import java.time.Instant;
import java.util.UUID;

public record ScheduleJourneyCommand(
        UUID trainId,
        UUID routeId,
        Instant departureTime
) {
}