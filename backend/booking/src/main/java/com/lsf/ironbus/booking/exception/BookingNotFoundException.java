package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class BookingNotFoundException
        extends DomainException {

    public BookingNotFoundException(String reference) {
        super(
                "BOOKING_NOT_FOUND",
                "Booking was not found: " + reference,
                HttpStatus.NOT_FOUND
        );
    }
}