package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidJourneyStatusTransitionException
        extends DomainException {

    public InvalidJourneyStatusTransitionException(
            JourneyStatus current,
            JourneyStatus target
    ) {
        super(
                "INVALID_JOURNEY_STATUS",
                "Journey status cannot be changed from %s to %s"
                        .formatted(current, target),
                HttpStatus.BAD_REQUEST
        );
    }
}