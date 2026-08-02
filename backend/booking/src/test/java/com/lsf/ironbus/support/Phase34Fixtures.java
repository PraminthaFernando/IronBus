package com.lsf.ironbus.support;

import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.domain.BookingReference;
import com.lsf.ironbus.booking.domain.BookingSegment;
import com.lsf.ironbus.booking.domain.PassengerDetails;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

public final class Phase34Fixtures {
    public static final Instant NOW = Instant.parse("2026-08-01T05:00:00Z");

    private Phase34Fixtures() {
    }

    public static String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static Train train() {
        return new Train(UUID.randomUUID(), "T-" + shortId(), "Test Train", NOW);
    }

    public static Route route() {
        return new Route(UUID.randomUUID(), "R-" + shortId(), "Test Route", NOW);
    }

    public static Station station(String p, String n) {
        return new Station(UUID.randomUUID(), p + shortId().substring(0, 4), n, NOW);
    }

    public static RouteStation rs(Route r, Station s, int seq, String km, int min) {
        return new RouteStation(UUID.randomUUID(), r, s, seq, new BigDecimal(km), min, NOW);
    }

    public static Coach reservedCoach(Train t, String no, TravelClass c) {
        return new Coach(UUID.randomUUID(), t, no, c, CoachReservationMode.RESERVED, NOW);
    }

    public static Seat seat(Coach c, String no, int row, int col) {
        return new Seat(UUID.randomUUID(), c, no, col == 1 ? SeatType.WINDOW : SeatType.AISLE, row, col, NOW);
    }

    public static Journey journey(Train t, Route r) {
        return new Journey(UUID.randomUUID(), t, r, NOW.plusSeconds(86400), NOW);
    }

    public static Booking booking(Journey j, Seat s, RouteStation o, RouteStation d, int os, int ds, String ref) {
        return new Booking(
            UUID.randomUUID(),
            new BookingReference(ref),
            j, s, o, d, os, ds,
            new PassengerDetails("Test Passenger", "test@example.com", "+94770000000"),
            new BigDecimal("1300.00"), Currency.getInstance("LKR"),
            NOW
        );
    }

    public static BookingSegment segment(Booking b, int seq) {
        return new BookingSegment(UUID.randomUUID(), b, b.getJourney(), b.getSeat(), seq);
    }
}
