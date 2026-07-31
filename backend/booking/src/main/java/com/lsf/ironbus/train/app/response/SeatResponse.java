package com.lsf.ironbus.train.app.response;

import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.enums.SeatType;

import java.util.UUID;

public record SeatResponse(
        UUID id,
        UUID coachId,
        String seatNumber,
        SeatType seatType,
        Integer rowNumber,
        Integer columnNumber,
        boolean active
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getCoach().getId(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                seat.getRowNumber(),
                seat.getColumnNumber(),
                seat.isActive()
        );
    }
}