package com.lsf.ironbus.route.web.controller;

import com.lsf.ironbus.route.app.command.AddStationToRouteCommand;
import com.lsf.ironbus.route.app.command.CreateRouteCommand;
import com.lsf.ironbus.route.app.response.RouteResponse;
import com.lsf.ironbus.route.app.response.RouteStationResponse;
import com.lsf.ironbus.route.app.service.AddStationToRouteService;
import com.lsf.ironbus.route.app.service.CreateRouteService;
import com.lsf.ironbus.route.web.request.AddStationToRouteRequest;
import com.lsf.ironbus.route.web.request.CreateRouteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final CreateRouteService createRouteService;
    private final AddStationToRouteService addStationToRouteService;

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
            @Valid @RequestBody CreateRouteRequest request
    ) {
        RouteResponse response = createRouteService.create(
                new CreateRouteCommand(
                        request.code(),
                        request.name()
                )
        );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/routes/" + response.id()
                ))
                .body(response);
    }

    @PostMapping("/{routeId}/stations")
    public ResponseEntity<RouteStationResponse> addStation(
            @PathVariable UUID routeId,
            @Valid @RequestBody AddStationToRouteRequest request
    ) {
        RouteStationResponse response =
                addStationToRouteService.add(
                        new AddStationToRouteCommand(
                                routeId,
                                request.stationId(),
                                request.sequenceNumber(),
                                request.distanceFromOriginKm(),
                                request.scheduledOffsetMinutes()
                        )
                );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/routes/"
                                + routeId
                                + "/stations/"
                                + response.id()
                ))
                .body(response);
    }
}