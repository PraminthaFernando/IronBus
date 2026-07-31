package com.lsf.ironbus.route.domain;

import com.lsf.ironbus.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RouteStationTest {

    private final Instant now = Instant.parse(
            "2026-07-31T10:00:00Z"
    );

    @Test
    void firstStationMustHaveZeroDistance() {
        Route route = route();
        Station station = station();

        assertThatThrownBy(() -> new RouteStation(
                UUID.randomUUID(),
                route,
                station,
                0,
                new BigDecimal("10.00"),
                0,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "first station distance must be zero"
                );
    }

    @Test
    void firstStationMustHaveZeroOffset() {
        assertThatThrownBy(() -> new RouteStation(
                UUID.randomUUID(),
                route(),
                station(),
                0,
                BigDecimal.ZERO,
                10,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "first station scheduled offset must be zero"
                );
    }

    @Test
    void laterStationCanHavePositiveDistanceAndOffset() {
        RouteStation routeStation = new RouteStation(
                UUID.randomUUID(),
                route(),
                station(),
                1,
                new BigDecimal("27.00"),
                35,
                now
        );

        assertThat(routeStation.getSequenceNumber()).isEqualTo(1);
        assertThat(routeStation.getDistanceFromOriginKm())
                .isEqualByComparingTo("27.00");
    }

    private Route route() {
        return new Route(
                UUID.randomUUID(),
                "FOT-BAD",
                "Colombo Fort to Badulla",
                now
        );
    }

    private Station station() {
        return new Station(
                UUID.randomUUID(),
                "FOT",
                "Colombo Fort",
                now
        );
    }
}