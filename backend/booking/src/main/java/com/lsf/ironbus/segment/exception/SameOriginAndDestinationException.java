package com.lsf.ironbus.segment.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class SameOriginAndDestinationException
        extends DomainException {

    public SameOriginAndDestinationException() {
        super(
                "SAME_ORIGIN_AND_DESTINATION",
                "Origin and destination must be different stations",
                HttpStatus.BAD_REQUEST
        );
    }
}