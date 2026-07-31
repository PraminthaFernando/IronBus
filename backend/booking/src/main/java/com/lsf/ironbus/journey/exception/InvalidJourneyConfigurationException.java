package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;

public class InvalidJourneyConfigurationException
        extends DomainException {

    public InvalidJourneyConfigurationException(String message) {
        super(
                "INVALID_JOURNEY_CONFIGURATION",
                message
        );
    }
}