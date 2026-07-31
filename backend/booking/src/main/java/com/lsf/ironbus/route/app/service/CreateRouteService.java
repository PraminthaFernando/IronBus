package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.app.command.CreateRouteCommand;
import com.lsf.ironbus.route.app.response.RouteResponse;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.exception.RouteCodeAlreadyExistsException;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreateRouteService {

    private final RouteRepository routeRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public RouteResponse create(CreateRouteCommand command) {
        String normalizedCode = normalizeCode(command.code());

        if (routeRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new RouteCodeAlreadyExistsException(normalizedCode);
        }

        Route route = new Route(
                uuidGenerator.generate(),
                normalizedCode,
                command.name(),
                timeProvider.now()
        );

        try {
            return RouteResponse.from(
                    routeRepository.saveAndFlush(route)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new RouteCodeAlreadyExistsException(normalizedCode);
        }
    }

    private String normalizeCode(String code) {
        if (code == null) {
            return null;
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }
}