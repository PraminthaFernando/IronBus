package com.lsf.ironbus.support;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentRange;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.train.domain.Train;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Phase2Fixtures {

    public static final Instant NOW = Instant.parse("2026-08-01T05:00:00Z");

    private Phase2Fixtures() {
    }

    public static Train train() {
        return new Train(UUID.randomUUID(), "T-" + shortId(), "Test Train", NOW);
    }

    public static Route route() {
        return new Route(UUID.randomUUID(), "R-" + shortId(), "Test Route", NOW);
    }

    public static Station station(String code, String name) {
        return new Station(UUID.randomUUID(), code, name, NOW);
    }

    public static RouteStation routeStation(
            Route route,
            Station station,
            int sequence,
            String distanceKm,
            int offsetMinutes
    ) {
        return new RouteStation(
                UUID.randomUUID(),
                route,
                station,
                sequence,
                new BigDecimal(distanceKm),
                offsetMinutes,
                NOW
        );
    }

    public static Journey scheduledJourney(Train train, Route route) {
        return new Journey(
                UUID.randomUUID(),
                train,
                route,
                NOW.plusSeconds(86_400),
                NOW
        );
    }

    public static JourneyLeg journeyLeg(
            int originSequence,
            int destinationSequence,
            String originDistance,
            String destinationDistance
    ) {
        return new JourneyLeg(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                originSequence,
                destinationSequence,
                new BigDecimal(originDistance),
                new BigDecimal(destinationDistance),
                new SegmentRange(originSequence, destinationSequence)
        );
    }

    private static String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
