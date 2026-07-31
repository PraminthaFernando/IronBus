package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.time.Instant;
import java.util.UUID;

public class TrainDepartureAlreadyScheduledException
        extends DomainException {

    public TrainDepartureAlreadyScheduledException(
            UUID trainId,
            Instant departureTime
    ) {
        super(
                "TRAIN_DEPARTURE_ALREADY_SCHEDULED",
                "Train %s already has a journey scheduled at %s"
                        .formatted(trainId, departureTime)
        );
    }
}