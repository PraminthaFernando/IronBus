package com.lsf.ironbus.segment.exception;

import com.lsf.ironbus.shared.error.DomainException;

public class InvalidJourneyDirectionException
        extends DomainException {

    public InvalidJourneyDirectionException(
            String originName,
            String destinationName
    ) {
        super(
                "INVALID_JOURNEY_DIRECTION",
                "Destination " + destinationName
                        + " must appear after origin "
                        + originName
        );
    }
}