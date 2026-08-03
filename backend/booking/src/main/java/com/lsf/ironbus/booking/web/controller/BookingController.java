package com.lsf.ironbus.booking.web.controller;

import com.lsf.ironbus.booking.app.command.CreateBookingCommand;
import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.response.BookingSearchResponse;
import com.lsf.ironbus.booking.app.service.CancelBookingService;
import com.lsf.ironbus.booking.app.service.CreateBookingService;
import com.lsf.ironbus.booking.app.service.GetBookingService;
import com.lsf.ironbus.booking.web.request.CreateBookingRequest;
import com.lsf.ironbus.booking.web.request.SearchBookingsRequest;
import com.lsf.ironbus.shared.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final CreateBookingService createBookingService;
    private final GetBookingService getBookingService;
    private final CancelBookingService cancelBookingService;

    @Operation(
        summary = "Create a booking",
        description = """
            Books one reserved seat for a specific journey leg.
            The booking endpoint is authoritative even when the seat
            was shown as available by a previous availability query.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Booking confirmed"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid booking request",
            content = @Content(schema =
            @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Journey, station, or seat not found",
            content = @Content(schema =
            @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Seat is unavailable or journey is not bookable",
            content = @Content(schema =
            @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody CreateBookingRequest request
    ) {
        BookingResponse response = createBookingService.create(
            new CreateBookingCommand(
                request.journeyId(),
                request.seatId(),
                request.originStationId(),
                request.destinationStationId(),
                request.passenger().name(),
                request.passenger().email(),
                request.passenger().phone()
            )
        );

        return ResponseEntity
            .created(URI.create(
                    "/api/v1/bookings/" + response.reference()
            ))
            .body(response);
    }

    @GetMapping("/{reference}")
    public BookingResponse get(
        @PathVariable String reference
    ) {
        return getBookingService.get(reference);
    }

    @PostMapping("/{reference}/cancel")
    public BookingResponse cancel(
        @PathVariable String reference
    ) {
        return cancelBookingService.cancel(reference);
    }

    @PostMapping("/search")
    public BookingSearchResponse search(
        @Valid
        @RequestBody
        SearchBookingsRequest request,

        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "10")
        int size
    ) {
        return getBookingService
            .searchByPassengerEmail(
                request.passengerEmail(),
                page,
                size
            );
    }
}