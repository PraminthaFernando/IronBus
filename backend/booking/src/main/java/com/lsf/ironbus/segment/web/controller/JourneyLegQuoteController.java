package com.lsf.ironbus.segment.web.controller;

import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.app.response.JourneyLegQuoteResponse;
import com.lsf.ironbus.segment.app.service.JourneyLegQuoteService;
import com.lsf.ironbus.train.enums.TravelClass;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
public class JourneyLegQuoteController {

    private final JourneyLegQuoteService quoteService;

    @GetMapping("/{journeyId}/quote")
    public JourneyLegQuoteResponse quote(
            @PathVariable UUID journeyId,
            @RequestParam UUID originStationId,
            @RequestParam UUID destinationStationId,
            @RequestParam TravelClass travelClass
    ) {
        return quoteService.quote(
                new ResolveJourneyLegCommand(
                        journeyId,
                        originStationId,
                        destinationStationId
                ),
                travelClass
        );
    }
}