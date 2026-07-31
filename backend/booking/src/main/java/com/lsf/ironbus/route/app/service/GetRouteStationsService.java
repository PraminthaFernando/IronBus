package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.app.response.RouteStationResponse;
import com.lsf.ironbus.route.app.response.RouteWithStationsResponse;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRouteStationsService {

    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;

    @Transactional(readOnly = true)
    public RouteWithStationsResponse get(UUID routeId) {
        Route route = routeRepository
                .findByIdAndActiveTrue(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route",
                        routeId
                ));

        List<RouteStationResponse> stations =
                routeStationRepository
                        .findAllByRouteIdAndActiveTrueOrderBySequenceNumberAsc(
                                routeId
                        )
                        .stream()
                        .map(RouteStationResponse::from)
                        .toList();

        return new RouteWithStationsResponse(
                route.getId(),
                route.getCode(),
                route.getName(),
                stations
        );
    }
}