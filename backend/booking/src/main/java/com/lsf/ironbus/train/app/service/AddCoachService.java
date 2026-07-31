package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.train.app.command.AddCoachCommand;
import com.lsf.ironbus.train.app.response.CoachResponse;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.exception.CoachNumberAlreadyExistsException;
import com.lsf.ironbus.train.repository.CoachRepository;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddCoachService {

    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public CoachResponse add(AddCoachCommand command) {
        Train train = trainRepository
                .findByIdAndActiveTrue(command.trainId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train",
                        command.trainId()
                ));

        if (coachRepository.existsByTrainIdAndCoachNumberIgnoreCase(
                command.trainId(),
                command.coachNumber()
        )) {
            throw new CoachNumberAlreadyExistsException(
                    command.trainId(),
                    command.coachNumber()
            );
        }

        Coach coach = new Coach(
                uuidGenerator.generate(),
                train,
                command.coachNumber(),
                command.travelClass(),
                command.reservationMode(),
                timeProvider.now()
        );

        try {
            return CoachResponse.from(
                    coachRepository.saveAndFlush(coach)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new CoachNumberAlreadyExistsException(
                    command.trainId(),
                    command.coachNumber()
            );
        }
    }
}