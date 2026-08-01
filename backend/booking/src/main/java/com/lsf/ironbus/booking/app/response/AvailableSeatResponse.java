package com.lsf.ironbus.booking.app.response;

import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;

import java.math.BigDecimal;
import java.util.UUID;

public record AvailableSeatResponse(
        UUID seatId,
        UUID coachId,
        String coachNumber,
        TravelClass travelClass,
        String seatNumber,
        SeatType seatType,
        Integer rowNumber,
        Integer columnNumber,
        BigDecimal fareAmount,
        String currency
) {
}