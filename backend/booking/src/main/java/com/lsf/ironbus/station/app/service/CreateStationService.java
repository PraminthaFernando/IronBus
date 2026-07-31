package com.lsf.ironbus.station.app.service;

import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.exception.StationCodeAlreadyExistsException;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.station.app.command.CreateStationCommand;
import com.lsf.ironbus.station.app.response.StationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreateStationService {

    private final StationRepository stationRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public StationResponse create(CreateStationCommand command) {
        String normalizedCode = normalizeCode(command.code());

        if (stationRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new StationCodeAlreadyExistsException(normalizedCode);
        }

        Station station = new Station(
                uuidGenerator.generate(),
                normalizedCode,
                command.name(),
                timeProvider.now()
        );

        try {
            Station saved = stationRepository.saveAndFlush(station);
            return StationResponse.from(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new StationCodeAlreadyExistsException(normalizedCode);
        }
    }

    private String normalizeCode(String code) {
        if (code == null) {
            return null;
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }
}