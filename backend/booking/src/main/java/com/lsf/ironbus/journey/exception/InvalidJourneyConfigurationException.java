package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidJourneyConfigurationException
        extends DomainException {

    public InvalidJourneyConfigurationException(String message) {
        super(
                "INVALID_JOURNEY_CONFIGURATION",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}