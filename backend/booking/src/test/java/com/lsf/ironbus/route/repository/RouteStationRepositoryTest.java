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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
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
}