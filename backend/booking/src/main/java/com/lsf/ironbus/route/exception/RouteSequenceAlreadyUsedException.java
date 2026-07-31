package com.lsf.ironbus.route.exception;

import com.lsf.ironbus.shared.error.DomainException;

import java.util.UUID;

public class RouteSequenceAlreadyUsedException
        extends DomainException {

    public RouteSequenceAlreadyUsedException(
            UUID routeId,
            int sequenceNumber
    ) {
        super(
                "ROUTE_SEQUENCE_ALREADY_USED",
                "Sequence " + sequenceNumber
                        + " is already used by route " + routeId
        );
    }
}