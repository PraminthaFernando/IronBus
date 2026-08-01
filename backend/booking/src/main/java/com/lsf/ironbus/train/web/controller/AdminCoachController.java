package com.lsf.ironbus.train.web.controller;

import com.lsf.ironbus.train.app.command.AddSeatCommand;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.app.service.AddSeatService;
import com.lsf.ironbus.train.web.request.AddSeatRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/coaches")
@RequiredArgsConstructor
public class AdminCoachController {

    private final AddSeatService addSeatService;

    @PostMapping("/{coachId}/seats")
    public ResponseEntity<SeatResponse> addSeat(
            @PathVariable UUID coachId,
            @Valid @RequestBody AddSeatRequest request
    ) {
        SeatResponse response = addSeatService.add(
                new AddSeatCommand(
                        coachId,
                        request.seatNumber(),
                        request.seatType(),
                        request.rowNumber(),
                        request.columnNumber()
                )
        );

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/admin/seats/" + response.id()
                ))
                .body(response);
    }
}