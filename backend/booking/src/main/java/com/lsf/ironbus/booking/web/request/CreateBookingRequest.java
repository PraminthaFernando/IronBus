package com.lsf.ironbus.booking.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBookingRequest(

        @NotNull(message = "Journey ID is required")
        UUID journeyId,

        @NotNull(message = "Seat ID is required")
        UUID seatId,

        @NotNull(message = "Origin station ID is required")
        UUID originStationId,

        @NotNull(message = "Destination station ID is required")
        UUID destinationStationId,

        @NotNull(message = "Passenger details are required")
        @Valid
        PassengerRequest passenger
) {
}