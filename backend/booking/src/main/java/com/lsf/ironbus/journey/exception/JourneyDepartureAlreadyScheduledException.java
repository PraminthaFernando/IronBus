package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
public class JourneyDepartureAlreadyScheduledException
        extends DomainException {

    private final UUID trainId;
    private final Instant departureTime;

    public JourneyDepartureAlreadyScheduledException(
            UUID trainId,
            Instant departureTime
    ) {
        super(
                "JOURNEY_DEPARTURE_ALREADY_SCHEDULED",
                "Train '%s' already has a journey scheduled at '%s'."
                        .formatted(trainId, departureTime),
                HttpStatus.BAD_REQUEST
        );

        this.trainId = trainId;
        this.departureTime = departureTime;
    }

}