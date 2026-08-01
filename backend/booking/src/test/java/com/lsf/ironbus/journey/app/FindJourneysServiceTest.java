package com.lsf.ironbus.journey.app;

import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.FindJourneysService;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.lsf.ironbus.support.Phase1BFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FindJourneysServiceTest {
    @Mock JourneyRepository journeyRepository;
    @Mock RouteRepository routeRepository;
    FindJourneysService service;

    @BeforeEach void setUp() { service = new FindJourneysService(journeyRepository, routeRepository); }

    @Test
    void returnsScheduledJourneysForSriLankanDate() {
        var train = train("FIND-JOURNEY-1");
        Route route = route();
        Journey journey = journey(train, route);
        when(routeRepository.existsById(route.getId())).thenReturn(true);
        when(journeyRepository.findAllByRouteIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndStatus(
                eq(route.getId()), any(), any(), eq(JourneyStatus.SCHEDULED)))
                .thenReturn(List.of(journey));
        var result = service.find(route.getId(), LocalDate.of(2026, 8, 2), ZoneId.of("Asia/Colombo"));
        assertThat(result).extracting(JourneyResponse::id).containsExactly(journey.getId());
    }
}
