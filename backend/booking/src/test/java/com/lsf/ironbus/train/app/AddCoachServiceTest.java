package com.lsf.ironbus.train.app;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.app.command.AddCoachCommand;
import com.lsf.ironbus.train.app.response.CoachResponse;
import com.lsf.ironbus.train.app.service.AddCoachService;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.exception.CoachNumberAlreadyExistsException;
import com.lsf.ironbus.train.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.NOW;
import static com.lsf.ironbus.support.Phase1BFixtures.train;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddCoachServiceTest {
    @Mock TrainRepository trainRepository;
    @Mock CoachRepository coachRepository;
    @Mock UuidGenerator uuidGenerator;
    @Mock TimeProvider timeProvider;
    AddCoachService service;

    @BeforeEach void setUp() { service = new AddCoachService(trainRepository, coachRepository, uuidGenerator, timeProvider); }

    @Test
    void addsCoachToActiveTrain() {
        Train train = train("COACH-SERVICE-1");
        UUID coachId = UUID.randomUUID();
        when(trainRepository.findByIdAndActiveTrue(train.getId())).thenReturn(Optional.of(train));
        when(coachRepository.existsByTrainIdAndCoachNumberIgnoreCase(train.getId(), "R1")).thenReturn(false);
        when(uuidGenerator.generate()).thenReturn(coachId);
        when(timeProvider.now()).thenReturn(NOW);
        when(coachRepository.saveAndFlush(any(Coach.class))).thenAnswer(i -> i.getArgument(0));
        CoachResponse response = service.add(new AddCoachCommand(train.getId(), "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED));
        assertThat(response.id()).isEqualTo(coachId);
        assertThat(response.trainId()).isEqualTo(train.getId());
    }

    @Test
    void rejectsDuplicateCoachNumber() {
        Train train = train("COACH-SERVICE-2");
        when(trainRepository.findByIdAndActiveTrue(train.getId())).thenReturn(Optional.of(train));
        when(coachRepository.existsByTrainIdAndCoachNumberIgnoreCase(train.getId(), "R1")).thenReturn(true);
        assertThatThrownBy(() -> service.add(new AddCoachCommand(train.getId(), "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED)))
                .isInstanceOf(CoachNumberAlreadyExistsException.class);
    }
}
