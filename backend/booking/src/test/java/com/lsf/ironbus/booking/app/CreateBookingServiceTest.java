package com.lsf.ironbus.booking.app;

import com.lsf.ironbus.booking.app.command.CreateBookingCommand;
import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.service.BookingMapper;
import com.lsf.ironbus.booking.app.service.CreateBookingService;
import com.lsf.ironbus.booking.domain.BookingReference;
import com.lsf.ironbus.booking.domain.BookingSegment;
import com.lsf.ironbus.booking.infra.BookingReferenceGenerator;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.booking.repository.BookingSegmentRepository;
import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentRange;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase34Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookingServiceTest {
    @Mock
    JourneyRepository journeys;

    @Mock
    SeatRepository seats;

    @Mock
    RouteStationRepository routeStations;

    @Mock
    BookingRepository bookings;

    @Mock
    BookingSegmentRepository segments;

    @Mock
    JourneyLegResolver resolver;

    @Mock
    FarePolicy farePolicy;

    @Mock
    BookingReferenceGenerator refs;

    @Mock
    UuidGenerator ids;

    @Mock
    TimeProvider time;

    @Mock
    BookingMapper mapper;

    CreateBookingService service;

    @BeforeEach
    void setup() {
        service = new CreateBookingService
            (
                journeys,
                seats,
                routeStations,
                bookings,
                segments,
                resolver,
                farePolicy,
                refs,
                ids,
                time,
                mapper
            );
    }

    @Test
    void createsAscendingSegments() {
        var t = train();
        var r = route();
        var j = journey(t, r);
        var s = seat(reservedCoach(t, "R1", TravelClass.SECOND_CLASS), "1A", 1, 1);
        var o = rs(r, station("A", "O"), 0, "0.00", 0);
        var d = rs(r, station("B", "D"), 3, "207.00", 240);
        var leg = new JourneyLeg(
            j.getId(),
            r.getId(),
            o.getId(),
            d.getId(),
            o.getStation().getId(),
            d.getStation().getId(),
            0,
            3,
            new BigDecimal("0.00"),
            new BigDecimal("207.00"),
            new SegmentRange(0, 3)
        );

        when(journeys.findDetailedById(j.getId())).thenReturn(Optional.of(j));
        when(resolver.resolve(any())).thenReturn(leg);
        when(seats.findDetailedActiveById(s.getId())).thenReturn(Optional.of(s));
        when(routeStations.findByIdAndActiveTrue(o.getId())).thenReturn(Optional.of(o));
        when(routeStations.findByIdAndActiveTrue(d.getId())).thenReturn(Optional.of(d));

        when(farePolicy.calculate(leg, TravelClass.SECOND_CLASS))
            .thenReturn(
                new Fare(
                    new BigDecimal("1756.00"),
                    Currency.getInstance("LKR")
                )
            );

        when(refs.generate()).thenReturn(new BookingReference("LSF-26-ABC123"));
        when(bookings.existsByReference("LSF-26-ABC123")).thenReturn(false);
        when(ids.generate())
            .thenReturn(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
            );
        when(time.now()).thenReturn(NOW);
        when(mapper.toResponse(any())).thenReturn(mock(BookingResponse.class));

        service.create(
            new CreateBookingCommand(
                j.getId(),
                s.getId(),
                o.getStation().getId(),
                d.getStation().getId(),
                "A",
                "a@b.com",
                "+94"
            )
        );

        ArgumentCaptor<List<BookingSegment>> c = ArgumentCaptor.forClass(List.class);
        verify(segments).saveAll(c.capture());
        assertThat(c.getValue())
            .extracting(BookingSegment::getSegmentSequence)
            .containsExactly(0, 1, 2);
        verify(segments).flush();
        verify(bookings).flush();
    }

    @Test
    void translatesConflict() {
        doThrow(new DataIntegrityViolationException("conflict")).when(segments).flush();
        assertThatThrownBy(() -> segments.flush()).isInstanceOf(DataIntegrityViolationException.class);
    }
}
