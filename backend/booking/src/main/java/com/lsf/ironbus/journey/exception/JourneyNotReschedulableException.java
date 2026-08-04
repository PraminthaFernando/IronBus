package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class JourneyNotReschedulableException
        extends DomainException {

    public JourneyNotReschedulableException(
            UUID journeyId,
            JourneyStatus status
    ) {
        super(
                "JOURNEY_NOT_RESHEDULABLE",
                "Journey '%s' cannot be rescheduled while its status is '%s'."
                        .formatted(journeyId, status),
                HttpStatus.UNAUTHORIZED
        );
    }
}