package com.lsf.ironbus.segment.application;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.domain.SegmentSequence;
import com.lsf.ironbus.segment.exception.InvalidJourneyDirectionException;
import com.lsf.ironbus.segment.exception.JourneyNotAvailableException;
import com.lsf.ironbus.segment.exception.SameOriginAndDestinationException;
import com.lsf.ironbus.segment.exception.StationNotOnRouteException;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase2Fixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JourneyLegResolverTest {

    @Mock
    JourneyRepository journeyRepository;

    @Mock
    RouteStationRepository routeStationRepository;

    JourneyLegResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new JourneyLegResolver(journeyRepository, routeStationRepository);
    }

    @Test
    void resolvesValidForwardJourneyLeg() {
        Route route = route();
        Journey journey = scheduledJourney(train(), route);
        Station originStation = station("FOT", "Colombo Fort");
        Station destinationStation = station("KDT", "Kandy");

        RouteStation origin = routeStation(route, originStation, 0, "0.00", 0);
        RouteStation destination = routeStation(route, destinationStation, 2, "120.00", 175);

        when(journeyRepository.findDetailedById(journey.getId()))
                .thenReturn(Optional.of(journey));
        when(routeStationRepository.findByRouteIdAndStationIdAndActiveTrue(
                route.getId(),
                originStation.getId()
        )).thenReturn(Optional.of(origin));
        when(routeStationRepository.findByRouteIdAndStationIdAndActiveTrue(
                route.getId(),
                destinationStation.getId()
        )).thenReturn(Optional.of(destination));

        var leg = resolver.resolve(new ResolveJourneyLegCommand(
                journey.getId(),
                originStation.getId(),
                destinationStation.getId()
        ));

        assertThat(leg.originSequence()).isZero();
        assertThat(leg.destinationSequence()).isEqualTo(2);
        assertThat(leg.distanceKm()).isEqualByComparingTo("120.00");
        assertThat(leg.segmentRange().segments())
                .extracting(SegmentSequence::value)
                .containsExactly(0, 1);
    }

    @Test
    void rejectsSameOriginAndDestination() {
        Journey journey = scheduledJourney(train(), route());
        UUID stationId = UUID.randomUUID();

        when(journeyRepository.findDetailedById(journey.getId()))
                .thenReturn(Optional.of(journey));

        assertThatThrownBy(() -> resolver.resolve(
                new ResolveJourneyLegCommand(
                        journey.getId(),
                        stationId,
                        stationId
                )
        )).isInstanceOf(SameOriginAndDestinationException.class);

        verifyNoInteractions(routeStationRepository);
    }

    @Test
    void rejectsStationOutsideRoute() {
        Route route = route();
        Journey journey = scheduledJourney(train(), route);
        UUID originId = UUID.randomUUID();

        when(journeyRepository.findDetailedById(journey.getId()))
                .thenReturn(Optional.of(journey));
        when(routeStationRepository.findByRouteIdAndStationIdAndActiveTrue(
                route.getId(),
                originId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve(
                new ResolveJourneyLegCommand(
                        journey.getId(),
                        originId,
                        UUID.randomUUID()
                )
        )).isInstanceOf(StationNotOnRouteException.class);
    }

    @Test
    void rejectsReverseDirection() {
        Route route = route();
        Journey journey = scheduledJourney(train(), route);
        Station kandy = station("KDT", "Kandy");
        Station colombo = station("FOT", "Colombo Fort");

        when(journeyRepository.findDetailedById(journey.getId()))
                .thenReturn(Optional.of(journey));
        when(routeStationRepository.findByRouteIdAndStationIdAndActiveTrue(
                route.getId(),
                kandy.getId()
        )).thenReturn(Optional.of(routeStation(route, kandy, 2, "120.00", 175)));
        when(routeStationRepository.findByRouteIdAndStationIdAndActiveTrue(
                route.getId(),
                colombo.getId()
        )).thenReturn(Optional.of(routeStation(route, colombo, 0, "0.00", 0)));

        assertThatThrownBy(() -> resolver.resolve(
                new ResolveJourneyLegCommand(
                        journey.getId(),
                        kandy.getId(),
                        colombo.getId()
                )
        )).isInstanceOf(InvalidJourneyDirectionException.class);
    }

    @Test
    void rejectsMissingJourney() {
        UUID journeyId = UUID.randomUUID();

        when(journeyRepository.findDetailedById(journeyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve(
                new ResolveJourneyLegCommand(
                        journeyId,
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        )).isInstanceOf(
                com.lsf.ironbus.shared.error.ResourceNotFoundException.class
        );
    }

    @Test
    void rejectsCancelledJourney() {
        Journey journey = scheduledJourney(train(), route());
        journey.cancel(NOW.plusSeconds(10));

        when(journeyRepository.findDetailedById(journey.getId()))
                .thenReturn(Optional.of(journey));

        assertThatThrownBy(() -> resolver.resolve(
                new ResolveJourneyLegCommand(
                        journey.getId(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        )).isInstanceOf(JourneyNotAvailableException.class);

        verifyNoInteractions(routeStationRepository);
    }
}
