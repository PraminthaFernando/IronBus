package com.lsf.ironbus.booking.app.command;

import java.util.UUID;

public record CreateBookingCommand(
        UUID journeyId,
        UUID seatId,
        UUID originStationId,
        UUID destinationStationId,
        String passengerName,
        String passengerEmail,
        String passengerPhone
) {
}