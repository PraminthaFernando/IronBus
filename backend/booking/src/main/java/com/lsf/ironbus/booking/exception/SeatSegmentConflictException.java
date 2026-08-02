package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class SeatSegmentConflictException
        extends DomainException {

    public SeatSegmentConflictException(
            UUID journeyId,
            UUID seatId
    ) {
        super(
                "SEAT_SEGMENT_CONFLICT",
                "The selected seat is no longer available for the requested journey leg"
        );
    }
}