package com.lsf.ironbus.booking;

import com.lsf.ironbus.booking.app.command.CreateBookingCommand;
import com.lsf.ironbus.booking.app.service.CreateBookingService;
import com.lsf.ironbus.booking.domain.BookingSegment;
import com.lsf.ironbus.booking.exception.SeatSegmentConflictException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
class BookingConcurrencyIntegrationTest extends PostgreSqlIntegrationTest {
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
    CreateBookingService service;

    @Autowired
    BookingSegmentRepository segments;

    @Autowired
    PlatformTransactionManager tm;

    UUID j, s, a, b, c;

    @BeforeEach
    void setup() {
        new TransactionTemplate(tm).executeWithoutResult(x -> {
            var t = trains.save(train());
            var r = routes.save(route());
            var sa = stations.save(station("A", "O"));
            var sb = stations.save(station("B", "M"));
            var sc = stations.save(station("C", "D"));
            routeStations.save(rs(r, sa, 0, "0.00", 0));
            routeStations.save(rs(r, sb, 1, "27.00", 35));
            routeStations.save(rs(r, sc, 2, "120.00", 175));
            var st = seats.save(
                seat(
                    coaches.save(
                        reservedCoach(t, "R1", TravelClass.SECOND_CLASS)
                    ),
                    "1A",
                    1,
                    1
                )
            );
            var jo = journeys.save(journey(t, r));
            journeys.flush();
            j = jo.getId();
            s = st.getId();
            a = sa.getId();
            b = sb.getId();
            c = sc.getId();
        });
    }

    @Test
    void oneWinnerForOverlappingRequests() throws Exception {
        int n = 10;
        ExecutorService ex = Executors.newFixedThreadPool(n);
        CountDownLatch ready = new CountDownLatch(n), start = new CountDownLatch(1);
        List<Future<Boolean>> fs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int k = i;
            fs.add(ex.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    service.create(new CreateBookingCommand(
                        j, s, a, c, "P", "p" + k + "@e.com", "+94")
                    );
                    return true;
                } catch (SeatSegmentConflictException e) {
                    return false;
                }
            }));
        }
        ready.await();
        start.countDown();
        int ok = 0;
        for (var f : fs) if (f.get()) ok++;
        ex.shutdownNow();
        assertThat(ok).isEqualTo(1);
        assertThat(
            segments.findAll().stream().filter(
                x ->
                    x.getJourney().getId().equals(j)
                        && x.getSeat().getId().equals(s)
            )
            .map(BookingSegment::getSegmentSequence)
            .sorted()
            .toList()
        )
        .containsExactly(0, 1);
    }

    @Test
    void adjacentRequestsBothSucceed() throws Exception {
        ExecutorService ex = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        var f1 = ex.submit(() -> {
            start.await();
            service.create(new CreateBookingCommand(
                j, s, a, b, "P1", "p1@e.com", "+94")
            );
            return true;
        });
        var f2 = ex.submit(() -> {
            start.await();
            service.create(new CreateBookingCommand(
                j, s, b, c, "P2", "p2@e.com", "+94")
            );
            return true;
        });
        start.countDown();
        assertThat(f1.get()).isTrue();
        assertThat(f2.get()).isTrue();
        ex.shutdownNow();
    }
}
