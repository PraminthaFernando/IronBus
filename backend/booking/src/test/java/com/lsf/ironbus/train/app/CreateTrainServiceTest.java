package com.lsf.ironbus.train.app;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.app.command.CreateTrainCommand;
import com.lsf.ironbus.train.app.response.TrainResponse;
import com.lsf.ironbus.train.app.service.CreateTrainService;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.exception.TrainCodeAlreadyExistsException;
import com.lsf.ironbus.train.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTrainServiceTest {
    @Mock TrainRepository trainRepository;
    @Mock UuidGenerator uuidGenerator;
    @Mock TimeProvider timeProvider;
    CreateTrainService service;

    @BeforeEach void setUp() { service = new CreateTrainService(trainRepository, uuidGenerator, timeProvider); }

    @Test
    void createsNormalizedTrain() {
        UUID id = UUID.randomUUID();
        when(uuidGenerator.generate()).thenReturn(id);
        when(timeProvider.now()).thenReturn(NOW);
        when(trainRepository.existsByCodeIgnoreCase("UDR-001")).thenReturn(false);
        when(trainRepository.saveAndFlush(any(Train.class))).thenAnswer(i -> i.getArgument(0));
        TrainResponse response = service.create(new CreateTrainCommand(" udr-001 ", "Udarata Menike"));
        ArgumentCaptor<Train> captor = ArgumentCaptor.forClass(Train.class);
        verify(trainRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("UDR-001");
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void rejectsDuplicateTrainCode() {
        when(trainRepository.existsByCodeIgnoreCase("UDR-001")).thenReturn(true);
        assertThatThrownBy(() -> service.create(new CreateTrainCommand("UDR-001", "Udarata Menike")))
                .isInstanceOf(TrainCodeAlreadyExistsException.class);
        verify(trainRepository, never()).saveAndFlush(any());
    }
}
