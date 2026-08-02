package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;

public class BookingAlreadyCancelledException
        extends DomainException {

    public BookingAlreadyCancelledException(String reference) {
        super(
                "BOOKING_ALREADY_CANCELLED",
                "Booking is already cancelled: " + reference
        );
    }
}