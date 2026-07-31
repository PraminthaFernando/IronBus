package com.lsf.ironbus.journey.web.controller;

import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.FindJourneysService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
public class JourneyQueryController {

    private static final ZoneId RAILWAY_ZONE =
            ZoneId.of("Asia/Colombo");

    private final FindJourneysService findJourneysService;

    @GetMapping
    public List<JourneyResponse> findJourneys(
            @RequestParam UUID routeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return findJourneysService.find(
                routeId,
                date,
                RAILWAY_ZONE
        );
    }
}