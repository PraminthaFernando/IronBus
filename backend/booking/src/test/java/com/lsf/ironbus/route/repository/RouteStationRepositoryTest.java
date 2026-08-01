package com.lsf.ironbus.route.repository;

import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase2Fixtures.NOW;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@ActiveProfiles("test")
class RouteStationRepositoryTest
        extends PostgreSqlIntegrationTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteStationRepository routeStationRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    void preventsDuplicateSequenceWithinSameRoute() {
        Instant now = Instant.parse("2026-07-31T10:00:00Z");

        Route route = routeRepository.save(new Route(
                UUID.randomUUID(),
                "TEST-ROUTE",
                "Test Route",
                now
        ));

        Station first = stationRepository.save(new Station(
                UUID.randomUUID(),
                "STA",
                "Station A",
                now
        ));

        Station second = stationRepository.save(new Station(
                UUID.randomUUID(),
                "STB",
                "Station B",
                now
        ));

        routeStationRepository.saveAndFlush(new RouteStation(
                UUID.randomUUID(),
                route,
                first,
                0,
                BigDecimal.ZERO,
                0,
                now
        ));

        assertThatThrownBy(() ->
                routeStationRepository.saveAndFlush(
                        new RouteStation(
                                UUID.randomUUID(),
                                route,
                                second,
                                0,
                                BigDecimal.ZERO,
                                0,
                                now
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findsRouteStationByRouteAndStation() {
        Route route = routeRepository.save(new Route(
                UUID.randomUUID(),
                "P2R-" + shortId(),
                "Phase 2 Route",
                NOW
        ));
        Station station = stationRepository.save(new Station(
                UUID.randomUUID(),
                "S" + shortId().substring(0, 5),
                "Phase 2 Station",
                NOW
        ));
        RouteStation routeStation = routeStationRepository.saveAndFlush(
                new RouteStation(
                        UUID.randomUUID(),
                        route,
                        station,
                        0,
                        BigDecimal.ZERO,
                        0,
                        NOW
                )
        );

        var found = routeStationRepository
                .findByRouteIdAndStationIdAndActiveTrue(
                        route.getId(),
                        station.getId()
                );

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getId()).isEqualTo(routeStation.getId());
    }

    @Test
    void preservesSequenceOrderAndDistancePrecision() {
        Route route = routeRepository.save(new Route(
                UUID.randomUUID(),
                "P2R-" + shortId(),
                "Ordered Route",
                NOW
        ));
        Station first = stationRepository.save(new Station(
                UUID.randomUUID(),
                "A" + shortId().substring(0, 5),
                "First",
                NOW
        ));
        Station second = stationRepository.save(new Station(
                UUID.randomUUID(),
                "B" + shortId().substring(0, 5),
                "Second",
                NOW
        ));

        routeStationRepository.save(new RouteStation(
                UUID.randomUUID(),
                route,
                second,
                1,
                new BigDecimal("27.35"),
                35,
                NOW
        ));
        routeStationRepository.save(new RouteStation(
                UUID.randomUUID(),
                route,
                first,
                0,
                BigDecimal.ZERO,
                0,
                NOW
        ));
        routeStationRepository.flush();

        var results = routeStationRepository
                .findAllByRouteIdAndActiveTrueOrderBySequenceNumberAsc(
                        route.getId()
                );

        assertThat(results)
                .extracting(RouteStation::getSequenceNumber)
                .containsExactly(0, 1);
        assertThat(results.get(1).getDistanceFromOriginKm())
                .isEqualByComparingTo("27.35");
    }

    private static String shortId() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}