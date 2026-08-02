package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.shared.error.DomainException;

public class BookingNotCancellableException
        extends DomainException {

    public BookingNotCancellableException(
            String reference,
            BookingStatus status
    ) {
        super(
                "BOOKING_NOT_CANCELLABLE",
                "Booking " + reference
                        + " cannot be cancelled while its status is "
                        + status
        );
    }
}