package com.lsf.ironbus.support;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.enums.CoachReservationMode;

import java.time.Instant;
import java.util.UUID;

public final class Phase1BFixtures {

    public static final Instant NOW =
            Instant.parse("2026-08-01T05:00:00Z");

    private Phase1BFixtures() {
    }

    public static Train train() {
        return train("T-" + shortId());
    }

    public static Train train(String code) {
        return new Train(
                UUID.randomUUID(),
                code,
                "Test Train",
                NOW
        );
    }

    public static Route route() {
        return new Route(
                UUID.randomUUID(),
                "R-" + shortId(),
                "Test Route",
                NOW
        );
    }

    public static Station station(String code, String name) {
        return new Station(
                UUID.randomUUID(),
                code,
                name,
                NOW
        );
    }

    public static Coach reservedCoach(
            Train train,
            String coachNumber
    ) {
        return new Coach(
                UUID.randomUUID(),
                train,
                coachNumber,
                TravelClass.SECOND_CLASS,
                CoachReservationMode.RESERVED,
                NOW
        );
    }

    public static Coach unreservedCoach(
            Train train,
            String coachNumber
    ) {
        return new Coach(
                UUID.randomUUID(),
                train,
                coachNumber,
                TravelClass.THIRD_CLASS,
                CoachReservationMode.UNRESERVED,
                NOW
        );
    }

    public static Seat seat(
            Coach coach,
            String seatNumber
    ) {
        return new Seat(
                UUID.randomUUID(),
                coach,
                seatNumber,
                SeatType.WINDOW,
                1,
                1,
                NOW
        );
    }

    public static Journey journey(
            Train train,
            Route route
    ) {
        return new Journey(
                UUID.randomUUID(),
                train,
                route,
                NOW.plusSeconds(86_400),
                NOW
        );
    }

    private static String shortId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}