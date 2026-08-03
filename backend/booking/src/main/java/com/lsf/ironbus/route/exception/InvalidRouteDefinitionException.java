package com.lsf.ironbus.route.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidRouteDefinitionException
        extends DomainException {

    private static final String DEFAULT_MESSAGE =
            "The route definition is invalid";

    public InvalidRouteDefinitionException(String message) {
        super(
            "INVALID_ROUTE_DEFINITION",
            normalizeMessage(message),
            HttpStatus.BAD_REQUEST
        );
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return DEFAULT_MESSAGE;
        }

        return message.trim();
    }
}