package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class JourneyNotDeletableException
        extends DomainException {

    public JourneyNotDeletableException(
            UUID journeyId,
            JourneyStatus status,
            String reason
    ) {
        super(
                "JOURNEY_NOT_DELETABLE",
                "Journey '%s' with status '%s' cannot be deleted because %s."
                        .formatted(
                                journeyId,
                                status,
                                reason
                        ),
                HttpStatus.UNAUTHORIZED
        );
    }
}