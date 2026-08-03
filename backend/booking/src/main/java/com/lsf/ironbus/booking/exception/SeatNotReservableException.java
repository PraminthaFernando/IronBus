package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class SeatNotReservableException
        extends DomainException {

    public SeatNotReservableException(UUID seatId) {
        super(
                "SEAT_NOT_RESERVABLE",
                "Seat cannot be individually reserved: " + seatId,
                HttpStatus.UNAUTHORIZED
        );
    }
}