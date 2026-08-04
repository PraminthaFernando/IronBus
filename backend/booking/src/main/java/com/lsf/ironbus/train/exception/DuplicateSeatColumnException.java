package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public final class DuplicateSeatColumnException
        extends DomainException {

    public static final String CODE =
        "DUPLICATE_SEAT_COLUMN";

    public DuplicateSeatColumnException() {
        super(
            CODE,
            "Seat column suffixes must be unique",
            HttpStatus.CONFLICT
        );
    }
}