package com.lsf.ironbus.train.domain;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SeatTest {

    private final Instant now =
            Instant.parse("2026-07-31T10:00:00Z");

    @Test
    void cannotCreateSeatForUnreservedCoach() {
        Train train = new Train(
                UUID.randomUUID(),
                "TEST-1",
                "Test Train",
                now
        );

        Coach coach = new Coach(
                UUID.randomUUID(),
                train,
                "U1",
                TravelClass.THIRD_CLASS,
                CoachReservationMode.UNRESERVED,
                now
        );

        assertThatThrownBy(() -> new Seat(
                UUID.randomUUID(),
                coach,
                "1A",
                SeatType.WINDOW,
                1,
                1,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "only be added to reserved coaches"
                );
    }
}