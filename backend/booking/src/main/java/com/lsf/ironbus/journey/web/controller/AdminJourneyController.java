package com.lsf.ironbus.journey.web.controller;

import com.lsf.ironbus.journey.app.command.RescheduleJourneyCommand;
import com.lsf.ironbus.journey.app.command.ScheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyAdminResponse;
import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.AdminJourneyService;
import com.lsf.ironbus.journey.app.service.ScheduleJourneyService;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.web.request.RescheduleJourneyRequest;
import com.lsf.ironbus.journey.web.request.ScheduleJourneyRequest;
import com.lsf.ironbus.journey.web.request.UpdateJourneyStatusRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/journeys")
@RequiredArgsConstructor
@Validated
public class AdminJourneyController {

    private final ScheduleJourneyService scheduleJourneyService;
    private final AdminJourneyService adminJourneyService;

    /**
     * Schedule a new train journey.
     */
    @PostMapping
    public ResponseEntity<JourneyResponse> schedule(
            @Valid @RequestBody ScheduleJourneyRequest request
    ) {
        JourneyResponse response = scheduleJourneyService.schedule(
                new ScheduleJourneyCommand(
                        request.trainId(),
                        request.routeId(),
                        request.departureTime()
                )
        );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/admin/journeys/" + response.id()
                ))
                .body(response);
    }

    /**
     * Search and list journeys.
     *
     * All filters are optional.
     */
    @GetMapping
    public ResponseEntity<Page<JourneyAdminResponse>> list(
            @RequestParam(required = false) UUID trainId,
            @RequestParam(required = false) UUID routeId,
            @RequestParam(required = false) JourneyStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDateTo,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "departureTime")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection
    ) {
        return ResponseEntity.ok(
                adminJourneyService.search(
                        trainId,
                        routeId,
                        status,
                        departureDateFrom,
                        departureDateTo,
                        page,
                        size,
                        sortBy,
                        sortDirection
                )
        );
    }

    /**
     * Retrieve one journey with admin-level details.
     */
    @GetMapping("/{journeyId}")
    public ResponseEntity<JourneyAdminResponse> getById(
            @PathVariable UUID journeyId
    ) {
        return ResponseEntity.ok(
                adminJourneyService.getById(journeyId)
        );
    }

    /**
     * Change the train, route or departure time before the journey starts.
     */
    @PutMapping("/{journeyId}")
    public ResponseEntity<JourneyAdminResponse> reschedule(
            @PathVariable UUID journeyId,
            @Valid @RequestBody RescheduleJourneyRequest request
    ) {
        JourneyAdminResponse response = adminJourneyService.reschedule(
                new RescheduleJourneyCommand(
                        journeyId,
                        request.trainId(),
                        request.routeId(),
                        request.departureTime(),
                        request.expectedVersion()
                )
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Perform a journey status transition.
     */
    @PatchMapping("/{journeyId}/status")
    public ResponseEntity<JourneyAdminResponse> updateStatus(
            @PathVariable UUID journeyId,
            @Valid @RequestBody UpdateJourneyStatusRequest request
    ) {
        return ResponseEntity.ok(
                adminJourneyService.updateStatus(
                        journeyId,
                        request.status(),
                        request.expectedVersion()
                )
        );
    }

    /**
     * Cancel a journey.
     *
     * A dedicated action endpoint makes the domain intention clearer than
     * treating cancellation as a normal field update.
     */
    @PostMapping("/{journeyId}/cancel")
    public ResponseEntity<JourneyAdminResponse> cancel(
            @PathVariable UUID journeyId,
            @RequestParam long expectedVersion
    ) {
        return ResponseEntity.ok(
                adminJourneyService.cancel(
                        journeyId,
                        expectedVersion
                )
        );
    }

    /**
     * Reactivate a cancelled journey when domain rules permit it.
     */
    @PostMapping("/{journeyId}/reactivate")
    public ResponseEntity<JourneyAdminResponse> reactivate(
            @PathVariable UUID journeyId,
            @RequestParam long expectedVersion
    ) {
        return ResponseEntity.ok(
                adminJourneyService.reactivate(
                        journeyId,
                        expectedVersion
                )
        );
    }

    /**
     * Permanently remove a journey.
     *
     * The application service should reject deletion when bookings or
     * operational records exist.
     */
    @DeleteMapping("/{journeyId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID journeyId,
            @RequestParam long expectedVersion
    ) {
        adminJourneyService.delete(
                journeyId,
                expectedVersion
        );

        return ResponseEntity.noContent().build();
    }
}