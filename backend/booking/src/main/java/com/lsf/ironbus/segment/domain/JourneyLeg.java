package com.lsf.ironbus.segment.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record JourneyLeg(
        UUID journeyId,
        UUID routeId,
        UUID originRouteStationId,
        UUID destinationRouteStationId,
        UUID originStationId,
        UUID destinationStationId,
        int originSequence,
        int destinationSequence,
        BigDecimal originDistanceKm,
        BigDecimal destinationDistanceKm,
        SegmentRange segmentRange
) {

    public JourneyLeg {
        Objects.requireNonNull(journeyId, "Journey id is required");
        Objects.requireNonNull(routeId, "Route id is required");
        Objects.requireNonNull(
                originRouteStationId,
                "Origin route station id is required"
        );
        Objects.requireNonNull(
                destinationRouteStationId,
                "Destination route station id is required"
        );
        Objects.requireNonNull(
                originStationId,
                "Origin station id is required"
        );
        Objects.requireNonNull(
                destinationStationId,
                "Destination station id is required"
        );
        Objects.requireNonNull(
                originDistanceKm,
                "Origin distance is required"
        );
        Objects.requireNonNull(
                destinationDistanceKm,
                "Destination distance is required"
        );
        Objects.requireNonNull(segmentRange, "Segment range is required");

        if (originSequence >= destinationSequence) {
            throw new IllegalArgumentException(
                    "Origin must appear before destination"
            );
        }

        if (destinationDistanceKm.compareTo(originDistanceKm) <= 0) {
            throw new IllegalArgumentException(
                    "Destination distance must be greater than origin distance"
            );
        }
    }

    public BigDecimal distanceKm() {
        return destinationDistanceKm.subtract(originDistanceKm);
    }
}