package com.lsf.ironbus.journey.app;

import com.lsf.ironbus.journey.app.command.ScheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.ScheduleJourneyService;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.exception.InvalidJourneyConfigurationException;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.*;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.repository.TrainRepository;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleJourneyServiceTest {
    @Mock JourneyRepository journeyRepository;
    @Mock TrainRepository trainRepository;
    @Mock RouteRepository routeRepository;
    @Mock RouteStationRepository routeStationRepository;
    @Mock UuidGenerator uuidGenerator;
    @Mock TimeProvider timeProvider;
    ScheduleJourneyService service;

    @BeforeEach void setUp() {
        service = new ScheduleJourneyService(journeyRepository, trainRepository, routeRepository,
                routeStationRepository, uuidGenerator, timeProvider);
    }

    @Test
    void schedulesJourneyForValidConfiguration() {
        Train train = train("JOURNEY-SERVICE-1");
        Route route = route();
        UUID journeyId = UUID.randomUUID();
        var departure = NOW.plusSeconds(86_400);
        when(trainRepository.findByIdAndActiveTrue(train.getId())).thenReturn(Optional.of(train));
        when(routeRepository.findByIdAndActiveTrue(route.getId())).thenReturn(Optional.of(route));
        when(routeStationRepository.countByRouteIdAndActiveTrue(route.getId())).thenReturn(2L);
        when(timeProvider.now()).thenReturn(NOW);
        when(journeyRepository.existsByTrainIdAndDepartureTime(train.getId(), departure)).thenReturn(false);
        when(uuidGenerator.generate()).thenReturn(journeyId);
        when(journeyRepository.saveAndFlush(any(Journey.class))).thenAnswer(i -> i.getArgument(0));
        JourneyResponse response = service.schedule(new ScheduleJourneyCommand(train.getId(), route.getId(), departure));
        assertThat(response.id()).isEqualTo(journeyId);
    }

    @Test
    void rejectsRouteWithFewerThanTwoStations() {
        Train train = train("JOURNEY-SERVICE-2");
        Route route = route();
        when(trainRepository.findByIdAndActiveTrue(train.getId())).thenReturn(Optional.of(train));
        when(routeRepository.findByIdAndActiveTrue(route.getId())).thenReturn(Optional.of(route));
        when(routeStationRepository.countByRouteIdAndActiveTrue(route.getId())).thenReturn(1L);
        assertThatThrownBy(() -> service.schedule(new ScheduleJourneyCommand(train.getId(), route.getId(), NOW.plusSeconds(86_400))))
                .isInstanceOf(InvalidJourneyConfigurationException.class);
    }
}
