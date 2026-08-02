package com.lsf.ironbus.booking;

import com.lsf.ironbus.booking.app.command.FindAvailableSeatsCommand;
import com.lsf.ironbus.booking.app.response.AvailableSeatResponse;
import com.lsf.ironbus.booking.app.service.CancelBookingService;
import com.lsf.ironbus.booking.app.service.FindAvailableSeatsService;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.booking.repository.BookingSegmentRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate",
        "app.fare.currency=LKR",
        "app.fare.base-fare=100.00",
        "app.fare.price-per-km=8.00",
        "app.fare.minimum-fare=150.00",
        "app.fare.class-multipliers.FIRST_CLASS=1.75",
        "app.fare.class-multipliers.SECOND_CLASS=1.25",
        "app.fare.class-multipliers.THIRD_CLASS=1.00"
    }
)
@Transactional
class BookingCancellationIntegrationTest extends PostgreSqlIntegrationTest {
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
    CancelBookingService cancel;

    @Autowired
    FindAvailableSeatsService availability;

    @Test
    void cancelReleasesSeat() {
        var t = trains.save(train());
        var r = routes.save(route());
        var a = stations.save(station("A", "O"));
        var b = stations.save(station("B", "D"));
        var ra = routeStations.save(rs(r, a, 0, "0.00", 0));
        var rb = routeStations.save(rs(r, b, 1, "27.00", 35));
        var s = seats.save(seat(coaches.save(reservedCoach(t, "R1", TravelClass.SECOND_CLASS)), "1A", 1, 1));
        var j = journeys.save(journey(t, r));
        var bk = bookings.save(booking(j, s, ra, rb, 0, 1, "LSF-26-ABC123"));
        segments.saveAndFlush(segment(bk, 0));

        assertThat
            (
                availability.find(
                    new FindAvailableSeatsCommand(
                        j.getId(),
                        a.getId(),
                        b.getId()
                    )
                )
                .seats()
            )
            .extracting(AvailableSeatResponse::seatId)
            .doesNotContain(s.getId());
        cancel.cancel("LSF-26-ABC123");
        assertThat
            (
                availability.find(new
                    FindAvailableSeatsCommand(
                        j.getId(),
                        a.getId(),
                        b.getId()
                    )
                ).seats()
            )
            .extracting(AvailableSeatResponse::seatId)
            .contains(s.getId());
    }
}
