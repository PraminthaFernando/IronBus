package com.lsf.ironbus.station.exception;

import com.lsf.ironbus.shared.error.DomainException;
import org.springframework.http.HttpStatus;

public class StationCodeAlreadyExistsException
        extends DomainException {

    public StationCodeAlreadyExistsException(String code) {
        super(
                "STATION_CODE_ALREADY_EXISTS",
                "A station already exists with code: " + code,
                HttpStatus.CONFLICT
        );
    }
}