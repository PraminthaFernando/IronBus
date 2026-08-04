package com.lsf.ironbus.train.web.controller;

import com.lsf.ironbus.shared.web.PageResponse;
import com.lsf.ironbus.train.app.command.AddSeatCommand;
import com.lsf.ironbus.train.app.response.CoachAdminResponse;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.app.service.AddSeatService;
import com.lsf.ironbus.train.app.service.AdminCoachService;
import com.lsf.ironbus.train.web.request.AddSeatRequest;
import com.lsf.ironbus.train.web.request.UpdateCoachRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCoachController {

    private final AddSeatService addSeatService;
    private final AdminCoachService adminCoachService;

    @GetMapping("/trains/{trainId}/coaches")
    public PageResponse<CoachAdminResponse> list(
            @PathVariable UUID trainId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.clamp(size, 1, 100),
                Sort.by(
                        Sort.Order.asc("coachNumber")
                )
        );

        return PageResponse.from(
                adminCoachService.listAdminByTrain(
                        trainId,
                        pageable
                ),
                response -> response
        );
    }

    @PostMapping("/coaches/{coachId}/seats")
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
                        request.columnNumber(),
                        request.active()
                )
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/admin/seats/"
                                        + response.id()
                        )
                )
                .body(response);
    }

    @GetMapping("/coaches/{coachId}")
    public CoachAdminResponse get(
            @PathVariable UUID coachId
    ) {
        return adminCoachService.getAdminResponse(coachId);
    }

    @PutMapping("/coaches/{coachId}")
    public CoachAdminResponse update(
            @PathVariable UUID coachId,
            @Valid @RequestBody UpdateCoachRequest request
    ) {
        return adminCoachService.update(
                coachId,
                request
        );
    }

    @PostMapping("/coaches/{coachId}/activate")
    public CoachAdminResponse activate(
            @PathVariable UUID coachId
    ) {
        return adminCoachService.setActive(
                coachId,
                true
        );
    }

    @PostMapping("/coaches/{coachId}/deactivate")
    public CoachAdminResponse deactivate(
            @PathVariable UUID coachId
    ) {
        return adminCoachService.setActive(
                coachId,
                false
        );
    }
}