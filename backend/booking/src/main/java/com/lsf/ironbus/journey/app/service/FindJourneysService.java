package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindJourneysService {

    private final JourneyRepository journeyRepository;
    private final RouteRepository routeRepository;

    @Transactional(readOnly = true)
    public List<JourneyResponse> find(
            UUID routeId,
            LocalDate date,
            ZoneId railwayZone
    ) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route", routeId);
        }

        Instant start = date
                .atStartOfDay(railwayZone)
                .toInstant();

        Instant end = date
                .plusDays(1)
                .atStartOfDay(railwayZone)
                .toInstant();

        return journeyRepository
                .findAllByRouteIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndStatus(
                        routeId,
                        start,
                        end,
                        JourneyStatus.SCHEDULED
                )
                .stream()
                .map(JourneyResponse::from)
                .toList();
    }
}