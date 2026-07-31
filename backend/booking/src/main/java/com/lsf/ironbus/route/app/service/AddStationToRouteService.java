package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.app.command.AddStationToRouteCommand;
import com.lsf.ironbus.route.app.response.RouteStationResponse;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.exception.InvalidRouteStationOrderException;
import com.lsf.ironbus.route.exception.RouteSequenceAlreadyUsedException;
import com.lsf.ironbus.route.exception.StationAlreadyInRouteException;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddStationToRouteService {

    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;
    private final StationRepository stationRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public RouteStationResponse add(
            AddStationToRouteCommand command
    ) {
        Route route = routeRepository
                .findByIdAndActiveTrue(command.routeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route",
                        command.routeId()
                ));

        Station station = stationRepository
                .findById(command.stationId())
                .filter(Station::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station",
                        command.stationId()
                ));

        assertStationNotAlreadyAssigned(command);
        assertSequenceNotAlreadyUsed(command);

        Optional<RouteStation> lastRouteStation =
                routeStationRepository
                        .findFirstByRouteIdAndActiveTrueOrderBySequenceNumberDesc(
                                route.getId()
                        );

        validateAppendOrder(command, lastRouteStation);

        RouteStation routeStation = new RouteStation(
                uuidGenerator.generate(),
                route,
                station,
                command.sequenceNumber(),
                command.distanceFromOriginKm(),
                command.scheduledOffsetMinutes(),
                timeProvider.now()
        );

        try {
            RouteStation saved =
                    routeStationRepository.saveAndFlush(routeStation);

            return RouteStationResponse.from(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidRouteStationOrderException(
                    "The route station could not be added because "
                            + "its station or sequence conflicts "
                            + "with existing route data"
            );
        }
    }

    private void assertStationNotAlreadyAssigned(
            AddStationToRouteCommand command
    ) {
        boolean exists =
                routeStationRepository.existsByRouteIdAndStationId(
                        command.routeId(),
                        command.stationId()
                );

        if (exists) {
            throw new StationAlreadyInRouteException(
                    command.routeId(),
                    command.stationId()
            );
        }
    }

    private void assertSequenceNotAlreadyUsed(
            AddStationToRouteCommand command
    ) {
        boolean exists =
                routeStationRepository.existsByRouteIdAndSequenceNumber(
                        command.routeId(),
                        command.sequenceNumber()
                );

        if (exists) {
            throw new RouteSequenceAlreadyUsedException(
                    command.routeId(),
                    command.sequenceNumber()
            );
        }
    }

    private void validateAppendOrder(
            AddStationToRouteCommand command,
            Optional<RouteStation> lastRouteStation
    ) {
        if (lastRouteStation.isEmpty()) {
            validateFirstStation(command);
            return;
        }

        RouteStation previous = lastRouteStation.get();

        int expectedSequence = previous.getSequenceNumber() + 1;

        if (command.sequenceNumber() != expectedSequence) {
            throw new InvalidRouteStationOrderException(
                    "The next route station sequence must be "
                            + expectedSequence
            );
        }

        if (command.distanceFromOriginKm().compareTo(
                previous.getDistanceFromOriginKm()
        ) <= 0) {
            throw new InvalidRouteStationOrderException(
                    "Distance from origin must increase for each station"
            );
        }

        if (command.scheduledOffsetMinutes()
                <= previous.getScheduledOffsetMinutes()) {
            throw new InvalidRouteStationOrderException(
                    "Scheduled offset must increase for each station"
            );
        }
    }

    private void validateFirstStation(
            AddStationToRouteCommand command
    ) {
        if (command.sequenceNumber() != 0) {
            throw new InvalidRouteStationOrderException(
                    "The first station sequence must be zero"
            );
        }

        if (command.distanceFromOriginKm()
                .compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidRouteStationOrderException(
                    "The first station distance must be zero"
            );
        }

        if (command.scheduledOffsetMinutes() != 0) {
            throw new InvalidRouteStationOrderException(
                    "The first station scheduled offset must be zero"
            );
        }
    }
}