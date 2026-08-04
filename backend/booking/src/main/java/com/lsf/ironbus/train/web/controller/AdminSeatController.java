package com.lsf.ironbus.train.web.controller;

import com.lsf.ironbus.shared.web.PageResponse;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.app.service.AdminSeatService;
import com.lsf.ironbus.train.web.request.BulkCreateSeatsRequest;
import com.lsf.ironbus.train.web.request.CreateSeatRequest;
import com.lsf.ironbus.train.web.request.UpdateSeatRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminSeatController {

    private final AdminSeatService service;

    @GetMapping(
            "/coaches/{coachId}/seats"
    )
    public PageResponse<SeatResponse> list(
            @PathVariable UUID coachId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.clamp(size, 1, 100),
                Sort.by(
                        Sort.Order.asc("rowNumber"),
                        Sort.Order.asc("columnNumber"),
                        Sort.Order.asc("seatNumber")
                )
        );

        return PageResponse.from(
                service.searchByCoach(
                        coachId,
                        search,
                        pageable
                ),
                SeatResponse::from
        );
    }

    @PostMapping(
            "/coaches/{coachId}/seats/bulk"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public List<SeatResponse> bulkCreate(
            @PathVariable UUID coachId,
            @Valid
            @RequestBody
            BulkCreateSeatsRequest request
    ) {
        return service.bulkCreate(coachId, request)
                .stream()
                .map(SeatResponse::from)
                .toList();
    }

    @GetMapping(
            "/seats/{seatId}"
    )
    public SeatResponse get(
            @PathVariable UUID seatId
    ) {
        return SeatResponse.from(
            service.getRequired(seatId)
        );
    }

    @PutMapping(
            "/seats/{seatId}"
    )
    public SeatResponse update(
            @PathVariable UUID seatId,
            @Valid
            @RequestBody
            UpdateSeatRequest request
    ) {
        return SeatResponse.from(
                service.update(seatId, request)
        );
    }

    @PostMapping(
            "/seats/{seatId}/activate"
    )
    public SeatResponse activate(
            @PathVariable UUID seatId
    ) {
        return SeatResponse.from(
                service.setActive(seatId, true)
        );
    }

    @PostMapping(
            "/seats/{seatId}/deactivate"
    )
    public SeatResponse deactivate(
            @PathVariable UUID seatId
    ) {
        return SeatResponse.from(
                service.setActive(seatId, false)
        );
    }
}