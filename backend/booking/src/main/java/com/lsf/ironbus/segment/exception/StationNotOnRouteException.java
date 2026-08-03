package com.lsf.ironbus.segment.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class StationNotOnRouteException extends DomainException {

    public StationNotOnRouteException(
            UUID stationId,
            UUID routeId
    ) {
        super(
                "STATION_NOT_ON_ROUTE",
                "Station " + stationId
                        + " does not belong to route " + routeId,
                HttpStatus.NOT_FOUND
        );
    }
}