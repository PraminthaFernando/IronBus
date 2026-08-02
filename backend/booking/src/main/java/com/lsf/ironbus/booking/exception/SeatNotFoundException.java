package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class SeatNotFoundException extends DomainException {

    public SeatNotFoundException(UUID seatId) {
        super(
                "SEAT_NOT_FOUND",
                "Seat was not found: " + seatId
        );
    }
}