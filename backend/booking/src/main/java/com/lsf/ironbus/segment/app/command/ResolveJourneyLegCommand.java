package com.lsf.ironbus.segment.app.command;

import java.util.UUID;

public record ResolveJourneyLegCommand(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId
) {
}