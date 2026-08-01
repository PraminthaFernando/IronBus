package com.lsf.ironbus.segment.infra.persistence;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentRange;
import com.lsf.ironbus.segment.exception.InvalidJourneyDirectionException;
import com.lsf.ironbus.segment.exception.JourneyNotAvailableException;
import com.lsf.ironbus.segment.exception.SameOriginAndDestinationException;
import com.lsf.ironbus.segment.exception.StationNotOnRouteException;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JourneyLegResolver {

    private final JourneyRepository journeyRepository;
    private final RouteStationRepository routeStationRepository;

    @Transactional(readOnly = true)
    public JourneyLeg resolve(
            ResolveJourneyLegCommand command
    ) {
        Journey journey = journeyRepository
                .findDetailedById(command.journeyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Journey",
                        command.journeyId()
                ));

        assertJourneyAvailable(journey);

        if (command.originStationId()
                .equals(command.destinationStationId())) {
            throw new SameOriginAndDestinationException();
        }

        RouteStation origin = routeStationRepository
                .findByRouteIdAndStationIdAndActiveTrue(
                        journey.getRoute().getId(),
                        command.originStationId()
                )
                .orElseThrow(() -> new StationNotOnRouteException(
                        command.originStationId(),
                        journey.getRoute().getId()
                ));

        RouteStation destination = routeStationRepository
                .findByRouteIdAndStationIdAndActiveTrue(
                        journey.getRoute().getId(),
                        command.destinationStationId()
                )
                .orElseThrow(() -> new StationNotOnRouteException(
                        command.destinationStationId(),
                        journey.getRoute().getId()
                ));

        if (origin.getSequenceNumber()
                >= destination.getSequenceNumber()) {
            throw new InvalidJourneyDirectionException(
                    origin.getStation().getName(),
                    destination.getStation().getName()
            );
        }

        SegmentRange segmentRange = new SegmentRange(
                origin.getSequenceNumber(),
                destination.getSequenceNumber()
        );

        return new JourneyLeg(
                journey.getId(),
                journey.getRoute().getId(),
                origin.getId(),
                destination.getId(),
                origin.getStation().getId(),
                destination.getStation().getId(),
                origin.getSequenceNumber(),
                destination.getSequenceNumber(),
                origin.getDistanceFromOriginKm(),
                destination.getDistanceFromOriginKm(),
                segmentRange
        );
    }

    private void assertJourneyAvailable(Journey journey) {
        if (journey.getStatus() == JourneyStatus.CANCELLED
                || journey.getStatus() == JourneyStatus.COMPLETED) {
            throw new JourneyNotAvailableException(journey.getId());
        }
    }
}