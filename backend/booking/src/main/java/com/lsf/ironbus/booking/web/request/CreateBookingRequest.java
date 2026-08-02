package com.lsf.ironbus.booking.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBookingRequest(

        @NotNull
        UUID journeyId,

        @NotNull
        UUID seatId,

        @NotNull
        UUID originStationId,

        @NotNull
        UUID destinationStationId,

        @NotNull
        @Valid
        PassengerRequest passenger
) {
}