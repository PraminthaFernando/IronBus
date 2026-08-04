package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class JourneyHasBookingsException extends DomainException {

    private final UUID journeyId;

    public JourneyHasBookingsException(UUID journeyId) {
        super(
                "JOURNEY_HAS_BOOKINGS",
                "Journey '%s' cannot be modified because it has existing bookings."
                        .formatted(journeyId),
                HttpStatus.BAD_REQUEST
        );
        this.journeyId = journeyId;
    }

}