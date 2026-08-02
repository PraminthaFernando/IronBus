package com.lsf.ironbus.booking.web.controller;

import com.lsf.ironbus.booking.app.command.CreateBookingCommand;
import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.service.CancelBookingService;
import com.lsf.ironbus.booking.app.service.CreateBookingService;
import com.lsf.ironbus.booking.app.service.GetBookingService;
import com.lsf.ironbus.booking.web.request.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final CreateBookingService createBookingService;
    private final GetBookingService getBookingService;
    private final CancelBookingService cancelBookingService;

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
}