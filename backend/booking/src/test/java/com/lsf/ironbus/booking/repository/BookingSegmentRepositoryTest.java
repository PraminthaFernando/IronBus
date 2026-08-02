package com.lsf.ironbus.booking.repository;

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
import org.springframework.dao.DataIntegrityViolationException;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {"spring.flyway.enabled=true", "spring.jpa.hibernate.ddl-auto=validate"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingSegmentRepositoryTest extends PostgreSqlIntegrationTest {

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


    @Test
    void uniqueJourneySeatSegment() {
        var t = trains.save(train());
        var r = routes.save(route());
        var o = routeStations.save(rs(r, stations.save(station("A", "O")), 0, "0.00", 0));
        var d = routeStations.save(rs(r, stations.save(station("B", "D")), 2, "120.00", 175));
        var s = seats.save(seat(coaches.save(reservedCoach(t, "R1", TravelClass.SECOND_CLASS)), "1A", 1, 1));
        var j = journeys.save(journey(t, r));
        var b1 = bookings.save(booking(j, s, o, d, 0, 2, "LSF-26-AAA111"));

        segments.saveAndFlush(segment(b1, 0));
        var b2 = bookings.save(booking(j, s, o, d, 0, 2, "LSF-26-BBB222"));

        assertThatThrownBy(
            () -> segments.saveAndFlush(
                segment(b2, 0)
            )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
