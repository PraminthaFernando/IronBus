package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class SeatNotAllowedForUnreservedCoachException
        extends DomainException {

    public SeatNotAllowedForUnreservedCoachException(UUID coachId) {
        super(
                "SEAT_NOT_ALLOWED_FOR_UNRESERVED_COACH",
                "Individual seats cannot be added to unreserved coach: "
                        + coachId
        );
    }
}