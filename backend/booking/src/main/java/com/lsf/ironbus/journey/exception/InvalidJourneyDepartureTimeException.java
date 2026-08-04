package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class InvalidJourneyDepartureTimeException
        extends DomainException {

    public InvalidJourneyDepartureTimeException(
            Instant departureTime
    ) {
        super(
                "INVALID_JOURNE_DEPARTURE_TIME",
                "Journey departure time '%s' must be in the future."
                        .formatted(departureTime),
                HttpStatus.BAD_REQUEST
        );
    }
}