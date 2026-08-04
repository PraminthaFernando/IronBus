package com.lsf.ironbus.station.app.service;

import com.lsf.ironbus.route.app.service.RouteStationService;
import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.station.app.response.StationResponse;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.exception.StationCodeAlreadyExistsException;
import com.lsf.ironbus.station.exception.StationNotFoundException;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.station.web.request.UpdateStationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditStationService {

    private final StationRepository stationRepository;
    private final RouteStationService routeStationService;

    @Transactional
    public StationResponse edit(
            UUID stationId,
            UpdateStationRequest request
    ) {
        Station station = stationRepository
                .findById(stationId)
                .orElseThrow(() ->
                        new StationNotFoundException(stationId)
                );

        assertVersion(
                station.getVersion(),
                request.expectedVersion()
        );

        String normalizedCode =
                normalizeCode(request.code());

        String normalizedName =
                normalizeName(request.name());

        assertCodeAvailable(
                stationId,
                normalizedCode
        );

        station.update(
                normalizedCode,
                normalizedName,
                request.active()
        );

        if(request.active()) {
            routeStationService.activateByStationId(stationId);
        } else {
            routeStationService.deactivateByStationId(stationId);
        }

        stationRepository.flush();

        return StationResponse.from(station);
    }

    private void assertCodeAvailable(
            UUID stationId,
            String code
    ) {
        stationRepository
                .findByCodeIgnoreCase(code)
                .filter(existing ->
                        !existing.getId().equals(stationId)
                )
                .ifPresent(existing -> {
                    throw new StationCodeAlreadyExistsException(
                            code
                    );
                });
    }

    private static void assertVersion(
            long actualVersion,
            long expectedVersion
    ) {
        if (actualVersion != expectedVersion) {
            throw new ResourceVersionConflictException(
                    "Station",
                    actualVersion,
                    expectedVersion
            );
        }
    }

    private static String normalizeCode(
            String code
    ) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Station code must not be blank"
            );
        }

        return code.trim()
                .toUpperCase(Locale.ROOT);
    }

    private static String normalizeName(
            String name
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Station name must not be blank"
            );
        }

        return name.trim();
    }
}