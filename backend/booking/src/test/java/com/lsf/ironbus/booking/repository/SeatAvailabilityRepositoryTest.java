package com.lsf.ironbus.booking.repository;

import com.lsf.ironbus.booking.infra.AvailableSeatProjection;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.repository.CoachRepository;
import com.lsf.ironbus.train.repository.SeatRepository;
import com.lsf.ironbus.train.repository.TrainRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=true", "spring.jpa.hibernate.ddl-auto=validate"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeatAvailabilityRepositoryTest extends PostgreSqlIntegrationTest {
    @Autowired
    TrainRepository trains;

    @Autowired
    RouteRepository routes;

    @Autowired
    StationRepository stations;

    @Autowired
    RouteStationRepository routeStations;

    @Autowired
    CoachRepository coaches;

    @Autowired
    SeatRepository seats;

    @Autowired
    JourneyRepository journeys;

    @Autowired
    BookingRepository bookings;

    @Autowired
    BookingSegmentRepository segments;

    @Autowired
    SeatAvailabilityRepository availability;

    @Test
    void overlapExcludedAdjacentAllowed() {
        var t = trains.save(train());
        var r = routes.save(route());
        var a = stations.save(station("A", "O"));
        var b = stations.save(station("B", "M"));
        var c = stations.save(station("C", "D"));
        var ra = routeStations.save(rs(r, a, 0, "0.00", 0));
        var rb = routeStations.save(rs(r, b, 1, "27.00", 35));

        routeStations.save(rs(r, c, 2, "120.00", 175));

        var s = seats.save(seat(coaches.save(reservedCoach(t, "R1", TravelClass.SECOND_CLASS)), "1A", 1, 1));
        var j = journeys.save(journey(t, r));
        var bk = bookings.save(booking(j, s, ra, rb, 0, 1, "LSF-26-ABC123"));

        segments.saveAndFlush(segment(bk, 0));

        assertThat
            (
                availability.findAvailableSeats(
                    j.getId(),
                    0,
                    2
                )
            )
            .extracting(
                AvailableSeatProjection::getSeatId
            ).doesNotContain(s.getId());

        assertThat
            (
                availability.findAvailableSeats(
                    j.getId(),
                    1,
                    2
                )
            )
            .extracting(
                AvailableSeatProjection::getSeatId
            ).contains(s.getId());
    }
}
