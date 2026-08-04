package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.app.response.RouteResponse;
import com.lsf.ironbus.route.app.response.RouteStationResponse;
import com.lsf.ironbus.route.app.response.RouteWithStationsResponse;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public List<RouteResponse> getAllActiveRoutes() {
        return routeRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(RouteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RouteResponse> getAllRoutes(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "name"
                )
        );

        return routeRepository
                .findAllByActiveTrueOrderByNameAsc(pageable)
                .map(RouteResponse::from);
    }
}