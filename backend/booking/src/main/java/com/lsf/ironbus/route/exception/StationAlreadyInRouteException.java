package com.lsf.ironbus.route.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class StationAlreadyInRouteException
        extends DomainException {

    public StationAlreadyInRouteException(
            UUID routeId,
            UUID stationId
    ) {
        super(
                "STATION_ALREADY_IN_ROUTE",
                "Station " + stationId
                        + " is already assigned to route " + routeId
        );
    }
}