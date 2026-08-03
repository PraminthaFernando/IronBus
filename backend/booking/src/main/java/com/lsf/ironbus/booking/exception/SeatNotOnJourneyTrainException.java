package com.lsf.ironbus.booking.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class SeatNotOnJourneyTrainException
        extends DomainException {

    public SeatNotOnJourneyTrainException(
            UUID seatId,
            UUID journeyId
    ) {
        super(
                "SEAT_NOT_ON_JOURNEY_TRAIN",
                "Seat " + seatId
                        + " does not belong to the train assigned to journey "
                        + journeyId,
                HttpStatus.CONFLICT
        );
    }
}