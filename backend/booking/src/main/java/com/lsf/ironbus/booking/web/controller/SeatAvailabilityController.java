package com.lsf.ironbus.booking.web.controller;

import com.lsf.ironbus.booking.app.command.FindAvailableSeatsCommand;
import com.lsf.ironbus.booking.app.response.AvailabilityResponse;
import com.lsf.ironbus.booking.app.service.FindAvailableSeatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
public class SeatAvailabilityController {

    private final FindAvailableSeatsService service;

    @GetMapping("/{journeyId}/available-seats")
    public AvailabilityResponse findAvailableSeats(
            @PathVariable UUID journeyId,
            @RequestParam UUID originStationId,
            @RequestParam UUID destinationStationId
    ) {
        return service.find(
                new FindAvailableSeatsCommand(
                        journeyId,
                        originStationId,
                        destinationStationId
                )
        );
    }
}