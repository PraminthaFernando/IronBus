package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class SeatNumberAlreadyExistsException extends DomainException {

    public SeatNumberAlreadyExistsException(
            UUID coachId,
            String seatNumber
    ) {
        super(
                "SEAT_NUMBER_ALREADY_EXISTS",
                "Seat number '%s' already exists for coach %s"
                        .formatted(seatNumber, coachId)
        );
    }
}