package com.lsf.ironbus.route.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class RouteCodeAlreadyExistsException
        extends DomainException {

    public RouteCodeAlreadyExistsException(String code) {
        super(
                "ROUTE_CODE_ALREADY_EXISTS",
                "A route already exists with code: " + code,
                HttpStatus.CONFLICT
        );
    }
}