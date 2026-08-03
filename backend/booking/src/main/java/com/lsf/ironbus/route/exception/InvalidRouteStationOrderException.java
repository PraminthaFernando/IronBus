package com.lsf.ironbus.route.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidRouteStationOrderException
        extends DomainException {

    public InvalidRouteStationOrderException(String message) {
        super(
            "INVALID_ROUTE_STATION_ORDER",
            message,
            HttpStatus.BAD_REQUEST
        );
    }
}