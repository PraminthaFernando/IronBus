package com.lsf.ironbus.booking.domain;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.booking.exception.BookingAlreadyCancelledException;
import com.lsf.ironbus.support.Phase34Fixtures;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {
    private Booking booking() {
        var t = train();
        var r = route();
        var o = rs(r, station("A", "Origin"), 0, "0.00", 0);
        var d = rs(r, station("B", "Dest"), 1, "27.00", 35);
        var s = seat(reservedCoach(t, "R1", TravelClass.SECOND_CLASS), "1A", 1, 1);
        return Phase34Fixtures.booking(journey(t, r), s, o, d, 0, 1, "LSF-26-ABC123");
    }

    @Test
    void startsConfirmed() {
        assertThat(booking().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void cancels() {
        var b = booking();
        b.cancel(NOW.plusSeconds(5));
        assertThat(b.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(b.getCancelledAt()).isEqualTo(NOW.plusSeconds(5));
    }

    @Test
    void rejectsSecondCancel() {
        var b = booking();
        b.cancel(NOW.plusSeconds(5));
        assertThatThrownBy(
            () -> b.cancel(
                NOW.plusSeconds(10)
            )
        )
        .isInstanceOf(BookingAlreadyCancelledException.class);
    }
}
