package com.lsf.ironbus.journey.web.controller;

import com.lsf.ironbus.journey.app.command.ScheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.ScheduleJourneyService;
import com.lsf.ironbus.journey.web.request.ScheduleJourneyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/journeys")
@RequiredArgsConstructor
public class AdminJourneyController {

    private final ScheduleJourneyService scheduleJourneyService;

    @PostMapping
    public ResponseEntity<JourneyResponse> schedule(
            @Valid @RequestBody ScheduleJourneyRequest request
    ) {
        JourneyResponse response =
                scheduleJourneyService.schedule(
                        new ScheduleJourneyCommand(
                                request.trainId(),
                                request.routeId(),
                                request.departureTime()
                        )
                );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/journeys/" + response.id()
                ))
                .body(response);
    }
}