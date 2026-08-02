package com.lsf.ironbus.route.web.controller;

import com.lsf.ironbus.route.app.response.RouteResponse;
import com.lsf.ironbus.route.app.service.GetRouteStationsService;
import com.lsf.ironbus.route.app.response.RouteWithStationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteQueryController {

    private final GetRouteStationsService getRouteStationsService;

    @GetMapping
    public List<RouteResponse> getAllActiveRoutes() {
        return getRouteStationsService.getAllActiveRoutes();
    }

    @GetMapping("/{routeId}/stations")
    public RouteWithStationsResponse getStations(
            @PathVariable UUID routeId
    ) {
        return getRouteStationsService.get(routeId);
    }
}