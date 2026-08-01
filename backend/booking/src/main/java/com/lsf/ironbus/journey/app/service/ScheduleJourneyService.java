package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.app.command.ScheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.exception.InvalidJourneyConfigurationException;
import com.lsf.ironbus.journey.exception.TrainDepartureAlreadyScheduledException;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ScheduleJourneyService {

    private final JourneyRepository journeyRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public JourneyResponse schedule(
            ScheduleJourneyCommand command
    ) {
        Train train = trainRepository
                .findByIdAndActiveTrue(command.trainId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train",
                        command.trainId()
                ));

        Route route = routeRepository
                .findByIdAndActiveTrue(command.routeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route",
                        command.routeId()
                ));

        long routeStationCount =
                routeStationRepository.countByRouteIdAndActiveTrue(
                        route.getId()
                );

        if (routeStationCount < 2) {
            throw new InvalidJourneyConfigurationException(
                    "A journey route must contain at least two active stations"
            );
        }

        Instant now = timeProvider.now();

        if (!command.departureTime().isAfter(now)) {
            throw new InvalidJourneyConfigurationException(
                    "Journey departure time must be in the future"
            );
        }

        if (journeyRepository.existsByTrainIdAndDepartureTime(
                train.getId(),
                command.departureTime()
        )) {
            throw new TrainDepartureAlreadyScheduledException(
                    train.getId(),
                    command.departureTime()
            );
        }

        Journey journey = new Journey(
                uuidGenerator.generate(),
                train,
                route,
                command.departureTime(),
                now
        );

        try {
            return JourneyResponse.from(
                    journeyRepository.saveAndFlush(journey)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new TrainDepartureAlreadyScheduledException(
                    train.getId(),
                    command.departureTime()
            );
        }
    }
}