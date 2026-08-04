package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class JourneyNotFoundException extends DomainException {

    private final UUID journeyId;

    public JourneyNotFoundException(UUID journeyId) {
        super(
                "JOURNEY_NOT_FOUND",
                "Journey with ID '%s' was not found."
                        .formatted(journeyId),
                HttpStatus.NOT_FOUND
        );

        this.journeyId = journeyId;
    }

}