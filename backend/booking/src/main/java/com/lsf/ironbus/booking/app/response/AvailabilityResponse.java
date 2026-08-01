package com.lsf.ironbus.booking.app.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AvailabilityResponse(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId,
        int originSequence,
        int destinationSequence,
        BigDecimal distanceKm,
        List<Integer> segmentSequences,
        List<AvailableSeatResponse> seats
) {

    public AvailabilityResponse {
        segmentSequences = List.copyOf(segmentSequences);
        seats = List.copyOf(seats);
    }
}