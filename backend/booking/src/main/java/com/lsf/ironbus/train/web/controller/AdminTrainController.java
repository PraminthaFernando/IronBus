package com.lsf.ironbus.train.web.controller;

import com.lsf.ironbus.train.app.command.AddCoachCommand;
import com.lsf.ironbus.train.app.command.CreateTrainCommand;
import com.lsf.ironbus.train.app.response.CoachResponse;
import com.lsf.ironbus.train.app.response.TrainResponse;
import com.lsf.ironbus.train.app.service.AddCoachService;
import com.lsf.ironbus.train.app.service.CreateTrainService;
import com.lsf.ironbus.train.web.request.AddCoachRequest;
import com.lsf.ironbus.train.web.request.CreateTrainRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/trains")
@RequiredArgsConstructor
public class AdminTrainController {

    private final CreateTrainService createTrainService;
    private final AddCoachService addCoachService;

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
                .created(URI.create(
                        "/api/v1/admin/trains/" + response.id()
                ))
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
                .created(URI.create(
                        "/api/v1/admin/coaches/" + response.id()
                ))
                .body(response);
    }
}