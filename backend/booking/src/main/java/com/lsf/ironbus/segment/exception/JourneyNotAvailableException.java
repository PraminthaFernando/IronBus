package com.lsf.ironbus.segment.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class JourneyNotAvailableException
        extends DomainException {

    public JourneyNotAvailableException(UUID journeyId) {
        super(
                "JOURNEY_NOT_AVAILABLE",
                "Journey is not available: " + journeyId,
                HttpStatus.NOT_FOUND
        );
    }
}