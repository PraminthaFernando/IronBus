package com.lsf.ironbus.station.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class StationNotFoundException extends DomainException {

    private static final String DEFAULT_MESSAGE =
            "The requested station was not found";

    private final UUID stationId;

    public StationNotFoundException(UUID stationId) {
        super(
            "STATION_NOT_FOUND",
            "Station was not found: " + stationId,
            HttpStatus.NOT_FOUND
        );
        this.stationId = stationId;
    }

    public StationNotFoundException(String message) {
        super(
            "STATION_NOT_FOUND",
            normalizeMessage(message),
            HttpStatus.NOT_FOUND
        );
        this.stationId = null;
    }

    public StationNotFoundException(
            UUID stationId,
            Throwable cause
    ) {
        super(
            "STATION_NOT_FOUND",
            "Station was not found: " + stationId,
            HttpStatus.NOT_FOUND
        );
        this.stationId = stationId;
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return DEFAULT_MESSAGE;
        }

        return message.trim();
    }
}