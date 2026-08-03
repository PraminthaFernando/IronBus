package com.lsf.ironbus.station.web.controller;

import com.lsf.ironbus.shared.web.PageResponse;
import com.lsf.ironbus.shared.web.PageableFactory;
import com.lsf.ironbus.station.app.command.CreateStationCommand;
import com.lsf.ironbus.station.app.response.StationResponse;
import com.lsf.ironbus.station.app.service.CreateStationService;
import com.lsf.ironbus.station.app.service.ListStationsService;
import com.lsf.ironbus.station.web.request.CreateStationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {

    private static final Set<String> SORTS =
            Set.of("code", "name", "active", "createdAt");

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
    public PageResponse<StationResponse> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code,asc") String sort
    ) {
        var pageable = PageableFactory.create(
                page,
                size,
                sort,
                SORTS,
                "code"
        );

        return PageResponse.from(
                listStationsService.search(search, pageable),
                StationResponse::from
        );
    }

    @GetMapping("/{stationId}")
    public StationResponse get(
            @PathVariable UUID stationId
    ) {
        return StationResponse.from(
                listStationsService.getRequired(stationId)
        );
    }

}