package com.lsf.ironbus.train.web.controller;

import com.lsf.ironbus.shared.web.PageResponse;
import com.lsf.ironbus.shared.web.PageableFactory;
import com.lsf.ironbus.train.app.command.AddCoachCommand;
import com.lsf.ironbus.train.app.command.CreateTrainCommand;
import com.lsf.ironbus.train.app.response.CoachResponse;
import com.lsf.ironbus.train.app.response.TrainAdminResponse;
import com.lsf.ironbus.train.app.response.TrainResponse;
import com.lsf.ironbus.train.app.service.AddCoachService;
import com.lsf.ironbus.train.app.service.AdminTrainService;
import com.lsf.ironbus.train.app.service.CreateTrainService;
import com.lsf.ironbus.train.web.request.AddCoachRequest;
import com.lsf.ironbus.train.web.request.CreateTrainRequest;
import com.lsf.ironbus.train.web.request.UpdateTrainRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/trains")
@RequiredArgsConstructor
public class AdminTrainController {

    private static final Set<String> ALLOWED_SORTS =
            Set.of(
                    "code",
                    "name",
                    "active",
                    "createdAt",
                    "updatedAt"
            );

    private final CreateTrainService createTrainService;
    private final AddCoachService addCoachService;
    private final AdminTrainService adminTrainService;

    @PostMapping
    public ResponseEntity<TrainResponse> createTrain(
            @Valid @RequestBody CreateTrainRequest request
    ) {
        TrainResponse response = createTrainService.create(
                new CreateTrainCommand(
                        request.code(),
                        request.name()
                )
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/admin/trains/"
                                        + response.id()
                        )
                )
                .body(response);
    }

    @PostMapping("/{trainId}/coaches")
    public ResponseEntity<CoachResponse> addCoach(
            @PathVariable UUID trainId,
            @Valid @RequestBody AddCoachRequest request
    ) {
        CoachResponse response = addCoachService.add(
                new AddCoachCommand(
                        trainId,
                        request.coachNumber(),
                        request.travelClass(),
                        request.reservationMode()
                )
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/admin/coaches/"
                                        + response.id()
                        )
                )
                .body(response);
    }

    @GetMapping("/{trainId}")
    public TrainAdminResponse get(
            @PathVariable UUID trainId
    ) {
        return adminTrainService.getAdminResponse(trainId);
    }

    @GetMapping
    public PageResponse<TrainAdminResponse> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code,asc") String sort
    ) {
        var pageable = PageableFactory.create(
                page,
                size,
                sort,
                ALLOWED_SORTS,
                "code"
        );

        return PageResponse.from(
                adminTrainService.searchAdmin(
                        search,
                        pageable
                ),
                response -> response
        );
    }

    @PutMapping("/{trainId}")
    public TrainAdminResponse update(
            @PathVariable UUID trainId,
            @Valid @RequestBody UpdateTrainRequest request
    ) {
        return adminTrainService.update(
                trainId,
                request
        );
    }

    @PostMapping("/{trainId}/activate")
    public TrainAdminResponse activate(
            @PathVariable UUID trainId
    ) {
        return adminTrainService.setActive(
                trainId,
                true
        );
    }

    @PostMapping("/{trainId}/deactivate")
    public TrainAdminResponse deactivate(
            @PathVariable UUID trainId
    ) {
        return adminTrainService.setActive(
                trainId,
                false
        );
    }
}