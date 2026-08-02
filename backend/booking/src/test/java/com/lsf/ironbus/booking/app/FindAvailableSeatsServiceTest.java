package com.lsf.ironbus.booking.app;

import com.lsf.ironbus.booking.app.command.FindAvailableSeatsCommand;
import com.lsf.ironbus.booking.app.service.FindAvailableSeatsService;
import com.lsf.ironbus.booking.infra.AvailableSeatProjection;
import com.lsf.ironbus.booking.repository.SeatAvailabilityRepository;
import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentRange;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAvailableSeatsServiceTest {
    @Mock
    JourneyLegResolver resolver;

    @Mock
    SeatAvailabilityRepository repo;

    @Mock
    FarePolicy farePolicy;

    @Mock
    AvailableSeatProjection projection;

    FindAvailableSeatsService service;

    @BeforeEach
    void setup() {
        service = new FindAvailableSeatsService(resolver, repo, farePolicy);
    }

    @Test
    void returnsSeatsWithFare() {
        UUID j = UUID.randomUUID(), o = UUID.randomUUID(), d = UUID.randomUUID();
        JourneyLeg leg = new
            JourneyLeg(
                j, UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                o, d, 0, 2,
                new BigDecimal("0.00"),
                new BigDecimal("120.00"),
                new SegmentRange(0, 2)
        );

        when(resolver.resolve(any())).thenReturn(leg);
        when(repo.findAvailableSeats(j, 0, 2)).thenReturn(List.of(projection));
        when(projection.getSeatId()).thenReturn(UUID.randomUUID());
        when(projection.getCoachId()).thenReturn(UUID.randomUUID());
        when(projection.getCoachNumber()).thenReturn("R1");
        when(projection.getTravelClass()).thenReturn(TravelClass.SECOND_CLASS);
        when(projection.getSeatNumber()).thenReturn("1A");
        when(projection.getSeatType()).thenReturn(SeatType.WINDOW);
        when(projection.getRowNumber()).thenReturn(1);
        when(projection.getColumnNumber()).thenReturn(1);
        when(farePolicy
            .calculate(
                leg,
                TravelClass.SECOND_CLASS)
            )
            .thenReturn(
                new Fare(
                        new BigDecimal("1300.00"),
                        Currency.getInstance("LKR")
                )
            );

        var out = service.find(new FindAvailableSeatsCommand(j, o, d));

        assertThat(out.segmentSequences()).containsExactly(0, 1);
        assertThat(out.seats()).hasSize(1);
        assertThat(
            out
                .seats()
                .getFirst()
                .fareAmount()
            )
            .isEqualByComparingTo("1300.00");
    }

    @Test
    void skipsQueryWhenResolutionFails() {
        when(resolver.resolve(any())).thenThrow(new IllegalArgumentException("bad"));
        assertThatThrownBy
            (
                () -> service.find(
                    new FindAvailableSeatsCommand(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                    )
                )
            )
            .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repo, farePolicy);
    }
}
