package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CoachNumberAlreadyExistsException extends DomainException {

    public CoachNumberAlreadyExistsException(
            UUID trainId,
            String coachNumber
    ) {
        super(
                "COACH_NUMBER_ALREADY_EXISTS",
                "Coach number '%s' already exists for train %s"
                        .formatted(coachNumber, trainId),
                HttpStatus.CONFLICT
        );
    }
}