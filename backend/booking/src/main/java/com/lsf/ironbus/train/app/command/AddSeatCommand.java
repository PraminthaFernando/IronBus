package com.lsf.ironbus.train.app.command;

import com.lsf.ironbus.train.enums.SeatType;

import java.util.UUID;

public record AddSeatCommand(
        UUID coachId,
        String seatNumber,
        SeatType seatType,
        Integer rowNumber,
        Integer columnNumber,
        boolean active
) {
}