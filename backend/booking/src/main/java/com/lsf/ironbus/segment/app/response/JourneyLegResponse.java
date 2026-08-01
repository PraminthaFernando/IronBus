package com.lsf.ironbus.segment.app.response;

import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentSequence;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record JourneyLegResponse(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId,
        int originSequence,
        int destinationSequence,
        BigDecimal distanceKm,
        List<Integer> segmentSequences
) {

    public JourneyLegResponse {
        segmentSequences = List.copyOf(segmentSequences);
    }

    public static JourneyLegResponse from(JourneyLeg leg) {
        return new JourneyLegResponse(
                leg.journeyId(),
                leg.originStationId(),
                leg.destinationStationId(),
                leg.originSequence(),
                leg.destinationSequence(),
                leg.distanceKm(),
                leg.segmentRange()
                        .segments()
                        .stream()
                        .map(SegmentSequence::value)
                        .toList()
        );
    }
}