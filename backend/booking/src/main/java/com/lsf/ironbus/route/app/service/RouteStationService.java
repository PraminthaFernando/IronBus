package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.route.web.request.ReplaceRouteStationsRequest;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.exception.StationNotFoundException;
import com.lsf.ironbus.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lsf.ironbus.station.app.service.ListStationsService.assertVersion;

@Service
@RequiredArgsConstructor
public class RouteStationService {

    private final RouteStationRepository routeStationRepository;
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final RouteStationDefinitionValidator validator;

    @Transactional
    public List<RouteStation> replaceStations(
            UUID routeId,
            ReplaceRouteStationsRequest request
    ) {
        Route route =getRequired(routeId);

        assertVersion(
                route.getVersion(),
                request.expectedVersion()
        );

        validator.validate(request.stations());

        List<UUID> stationIds = request.stations()
                .stream()
                .map(ReplaceRouteStationsRequest.RouteStationItem::stationId)
                .toList();

        Map<UUID, Station> stations = stationRepository
                .findAllById(stationIds)
                .stream()
                .collect(Collectors.toMap(
                        Station::getId,
                        Function.identity()
                ));

        if (stations.size() != stationIds.size()) {
            throw new StationNotFoundException(
                    "One or more route stations do not exist"
            );
        }

        routeStationRepository.deleteByRouteId(routeId);
        routeStationRepository.flush();

        List<RouteStation> replacements =
                request.stations()
                        .stream()
                        .map(item -> RouteStation.create(
                                route,
                                stations.get(item.stationId()),
                                item.sequenceNumber(),
                                item.distanceFromOriginKm(),
                                item.scheduledOffsetMinutes()
                        ))
                        .toList();

        List<RouteStation> saved =
                routeStationRepository.saveAll(replacements);

        route.touch();

        return saved;
    }

    @Transactional
    public void deactivateByStationId(UUID stationId) {
        List<RouteStation> routeStations =
                routeStationRepository.findAllByStationId(
                        stationId
                );

        routeStations.stream()
                .filter(RouteStation::isActive)
                .forEach(RouteStation::deactivate);

    }

    @Transactional
    public void activateByStationId(UUID stationId) {
        List<RouteStation> routeStations =
                routeStationRepository.findAllByStationId(
                        stationId
                );

        routeStations.stream()
                .filter(routeStation ->
                        !routeStation.isActive()
                )
                .forEach(RouteStation::activate);

    }

    private Route getRequired(UUID routeId) {
        return routeRepository.findById(routeId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Route",
                        routeId
                )
        );
    }
}
