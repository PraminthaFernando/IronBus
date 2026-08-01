package com.lsf.ironbus.booking.app.command;

import java.util.UUID;

public record FindAvailableSeatsCommand(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId
) {
}