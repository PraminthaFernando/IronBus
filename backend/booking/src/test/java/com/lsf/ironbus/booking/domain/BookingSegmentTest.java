package com.lsf.ironbus.booking.domain;

import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingSegmentTest {
    @Test
    void rejectsNegative() {
        var t = train();
        var r = route();
        var o = rs(r, station("A", "O"), 0, "0.00", 0);
        var d = rs(r, station("B", "D"), 1, "27.00", 35);
        var s = seat(reservedCoach(t, "R1", TravelClass.SECOND_CLASS), "1A", 1, 1);
        var b = booking(journey(t, r), s, o, d, 0, 1, "LSF-26-ABC123");

        assertThatThrownBy(
            () -> new BookingSegment(
                java.util.UUID.randomUUID(),
                b,
                b.getJourney(),
                b.getSeat(),
                -1
            )
        )
        .isInstanceOf(IllegalArgumentException.class);
    }
}
