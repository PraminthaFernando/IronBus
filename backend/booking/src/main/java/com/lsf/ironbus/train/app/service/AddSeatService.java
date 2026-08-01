package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.train.app.command.AddSeatCommand;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.exception.SeatNotAllowedForUnreservedCoachException;
import com.lsf.ironbus.train.exception.SeatNumberAlreadyExistsException;
import com.lsf.ironbus.train.repository.CoachRepository;
import com.lsf.ironbus.train.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddSeatService {

    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public SeatResponse add(AddSeatCommand command) {
        Coach coach = coachRepository
                .findByIdAndActiveTrue(command.coachId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coach",
                        command.coachId()
                ));

        if (!coach.isReserved()) {
            throw new SeatNotAllowedForUnreservedCoachException(
                    coach.getId()
            );
        }

        if (seatRepository.existsByCoachIdAndSeatNumberIgnoreCase(
                coach.getId(),
                command.seatNumber()
        )) {
            throw new SeatNumberAlreadyExistsException(
                    coach.getId(),
                    command.seatNumber()
            );
        }

        Seat seat = new Seat(
                uuidGenerator.generate(),
                coach,
                command.seatNumber(),
                command.seatType(),
                command.rowNumber(),
                command.columnNumber(),
                timeProvider.now()
        );

        try {
            return SeatResponse.from(
                    seatRepository.saveAndFlush(seat)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new SeatNumberAlreadyExistsException(
                    coach.getId(),
                    command.seatNumber()
            );
        }
    }
}