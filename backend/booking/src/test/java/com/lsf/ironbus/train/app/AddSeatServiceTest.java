package com.lsf.ironbus.train.app;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.app.command.AddSeatCommand;
import com.lsf.ironbus.train.app.service.AddSeatService;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.exception.SeatNotAllowedForUnreservedCoachException;
import com.lsf.ironbus.train.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSeatServiceTest {
    @Mock CoachRepository coachRepository;
    @Mock SeatRepository seatRepository;
    @Mock UuidGenerator uuidGenerator;
    @Mock TimeProvider timeProvider;
    AddSeatService service;

    @BeforeEach void setUp() { service = new AddSeatService(coachRepository, seatRepository, uuidGenerator, timeProvider); }

    @Test
    void addsSeatToReservedCoach() {
        Coach coach = reservedCoach(train("SEAT-SERVICE-1"), "R1");
        UUID seatId = UUID.randomUUID();
        when(coachRepository.findByIdAndActiveTrue(coach.getId())).thenReturn(Optional.of(coach));
        when(seatRepository.existsByCoachIdAndSeatNumberIgnoreCase(coach.getId(), "1A")).thenReturn(false);
        when(uuidGenerator.generate()).thenReturn(seatId);
        when(timeProvider.now()).thenReturn(NOW);
        when(seatRepository.saveAndFlush(any(Seat.class))).thenAnswer(i -> i.getArgument(0));
        SeatResponse response = service.add(new AddSeatCommand(coach.getId(), "1A", SeatType.WINDOW, 1, 1));
        assertThat(response.id()).isEqualTo(seatId);
        assertThat(response.seatNumber()).isEqualTo("1A");
    }

    @Test
    void rejectsSeatForUnreservedCoach() {
        Coach coach = unreservedCoach(train("SEAT-SERVICE-2"), "U1");
        when(coachRepository.findByIdAndActiveTrue(coach.getId())).thenReturn(Optional.of(coach));
        assertThatThrownBy(() -> service.add(new AddSeatCommand(coach.getId(), "1A", SeatType.WINDOW, 1, 1)))
                .isInstanceOf(SeatNotAllowedForUnreservedCoachException.class);
        verify(seatRepository, never()).saveAndFlush(any());
    }
}
