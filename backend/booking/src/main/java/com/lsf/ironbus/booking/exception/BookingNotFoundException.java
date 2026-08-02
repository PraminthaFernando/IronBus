package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;

public class BookingNotFoundException
        extends DomainException {

    public BookingNotFoundException(String reference) {
        super(
                "BOOKING_NOT_FOUND",
                "Booking was not found: " + reference
        );
    }
}