package com.lsf.ironbus.segment.app.service;

import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.app.response.JourneyLegQuoteResponse;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentSequence;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.train.enums.TravelClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JourneyLegQuoteService {

    private final JourneyLegResolver journeyLegResolver;
    private final FarePolicy farePolicy;

    public JourneyLegQuoteResponse quote(
            ResolveJourneyLegCommand command,
            TravelClass travelClass
    ) {
        JourneyLeg leg = journeyLegResolver.resolve(command);

        Fare fare = farePolicy.calculate(leg, travelClass);

        return new JourneyLegQuoteResponse(
                leg.journeyId(),
                leg.originStationId(),
                leg.destinationStationId(),
                leg.originSequence(),
                leg.destinationSequence(),
                leg.segmentRange()
                        .segments()
                        .stream()
                        .map(SegmentSequence::value)
                        .toList(),
                leg.distanceKm(),
                travelClass,
                fare.amount(),
                fare.currency().getCurrencyCode()
        );
    }
}