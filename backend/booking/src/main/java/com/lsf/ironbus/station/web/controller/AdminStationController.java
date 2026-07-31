package com.lsf.ironbus.station.web.controller;

import com.lsf.ironbus.station.app.command.CreateStationCommand;
import com.lsf.ironbus.station.app.response.StationResponse;
import com.lsf.ironbus.station.app.service.CreateStationService;
import com.lsf.ironbus.station.app.service.ListStationsService;
import com.lsf.ironbus.station.web.request.CreateStationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {

    private final CreateStationService createStationService;
    private final ListStationsService listStationsService;

    @PostMapping
    public ResponseEntity<StationResponse> create(
            @Valid @RequestBody CreateStationRequest request
    ) {
        StationResponse response = createStationService.create(
                new CreateStationCommand(
                        request.code(),
                        request.name()
                )
        );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/admin/stations/" + response.id()
                ))
                .body(response);
    }

    @GetMapping
    public List<StationResponse> list() {
        return listStationsService.listActive();
    }
}