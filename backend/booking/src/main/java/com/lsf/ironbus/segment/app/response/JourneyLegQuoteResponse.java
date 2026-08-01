package com.lsf.ironbus.segment.app.response;

import com.lsf.ironbus.train.enums.TravelClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record JourneyLegQuoteResponse(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId,
        int originSequence,
        int destinationSequence,
        List<Integer> segmentSequences,
        BigDecimal distanceKm,
        TravelClass travelClass,
        BigDecimal fareAmount,
        String currency
) {

    public JourneyLegQuoteResponse {
        segmentSequences = List.copyOf(segmentSequences);
    }
}