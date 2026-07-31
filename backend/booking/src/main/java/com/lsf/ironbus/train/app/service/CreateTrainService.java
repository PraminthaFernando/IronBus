package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.app.command.CreateTrainCommand;
import com.lsf.ironbus.train.app.response.TrainResponse;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.exception.TrainCodeAlreadyExistsException;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreateTrainService {

    private final TrainRepository trainRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public TrainResponse create(CreateTrainCommand command) {
        String code = normalize(command.code());

        if (trainRepository.existsByCodeIgnoreCase(code)) {
            throw new TrainCodeAlreadyExistsException(code);
        }

        Train train = new Train(
                uuidGenerator.generate(),
                code,
                command.name(),
                timeProvider.now()
        );

        try {
            return TrainResponse.from(
                    trainRepository.saveAndFlush(train)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new TrainCodeAlreadyExistsException(code);
        }
    }

    private String normalize(String code) {
        return code == null
                ? null
                : code.trim().toUpperCase(Locale.ROOT);
    }
}