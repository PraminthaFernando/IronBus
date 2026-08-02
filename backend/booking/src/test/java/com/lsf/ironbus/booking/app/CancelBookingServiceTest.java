package com.lsf.ironbus.booking.app;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.service.BookingMapper;
import com.lsf.ironbus.booking.app.service.CancelBookingService;
import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.booking.repository.BookingSegmentRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.support.Phase34Fixtures;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelBookingServiceTest {
    @Mock
    BookingRepository bookings;

    @Mock
    BookingSegmentRepository segments;

    @Mock
    BookingMapper mapper;

    @Mock
    TimeProvider time;

    CancelBookingService service;

    @BeforeEach
    void setup() {
        service = new CancelBookingService(bookings, segments, mapper, time);
    }

    private Booking booking() {
        var t = train();
        var r = route();
        var o = rs(r, station("A", "O"), 0, "0.00", 0);
        var d = rs(r, station("B", "D"), 1, "27.00", 35);
        var s = seat(
                reservedCoach(t, "R1", TravelClass.SECOND_CLASS), "1A", 1, 1);
        return Phase34Fixtures.booking(journey(t, r), s, o, d, 0, 1, "LSF-26-ABC123");
    }

    @Test
    void releasesSegments() {
        var b = booking();
        when(bookings.findDetailedByReference("LSF-26-ABC123")).thenReturn(Optional.of(b));
        when(time.now()).thenReturn(NOW.plusSeconds(5));
        when(segments.deleteAllByBookingId(b.getId())).thenReturn(1);
        when(mapper.toResponse(b)).thenReturn(mock(BookingResponse.class));
        service.cancel("lsf-26-abc123");
        assertThat(b.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(segments).deleteAllByBookingId(b.getId());
    }
}
