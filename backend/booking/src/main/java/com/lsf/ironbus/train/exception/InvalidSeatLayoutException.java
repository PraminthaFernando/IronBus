package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public final class InvalidSeatLayoutException
        extends DomainException {

    public static final String CODE =
        "INVALID_SEAT_LAYOUT";

    private static final String DEFAULT_MESSAGE =
        "The seat layout is invalid";

    public InvalidSeatLayoutException(String message) {
        super(
            CODE,
            normalizeMessage(message),
            HttpStatus.BAD_REQUEST
        );
    }

    private static String normalizeMessage(
            String message
    ) {
        if (message == null || message.isBlank()) {
            return DEFAULT_MESSAGE;
        }

        return message.trim();
    }
}