package com.lsf.ironbus.booking.app.response;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.train.enums.TravelClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String reference,
        BookingStatus status,
        UUID journeyId,
        Instant departureTime,
        UUID seatId,
        String coachNumber,
        String seatNumber,
        TravelClass travelClass,
        String originCode,
        String originName,
        String destinationCode,
        String destinationName,
        BigDecimal fareAmount,
        String currency,
        Instant createdAt
) {
}