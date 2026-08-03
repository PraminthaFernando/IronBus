package com.lsf.ironbus.station.app.service;

import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.shared.infra.SystemTimeProvider;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.station.app.response.StationResponse;
import com.lsf.ironbus.station.web.request.UpdateStationRequest;
import com.lsf.ironbus.station.exception.StationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListStationsService {

    private final StationRepository stationRepository;
    private final SystemTimeProvider timeProvider;

    @Transactional(readOnly = true)
    public List<StationResponse> listActive() {
        return stationRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(StationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Station> search(
            String search,
            Pageable pageable
    ) {
        String query = search == null ? "" : search.trim();

        if (query.isEmpty()) {
            return stationRepository.findAll(pageable);
        }

        return stationRepository
                .findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                        query,
                        query,
                        pageable
                );
    }

    @Transactional(readOnly = true)
    public Station getRequired(UUID stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() ->
                        new StationNotFoundException(stationId)
                );
    }

    @Transactional
    public Station update(
            UUID stationId,
            UpdateStationRequest request
    ) {
        Station station = getRequired(stationId);

        assertVersion(
                station.getVersion(),
                request.expectedVersion()
        );

        station.update(
                normalizeCode(request.code()),
                request.name().trim(),
                request.active()
        );

        return station;
    }

    @Transactional
    public Station setActive(
            UUID stationId,
            boolean active
    ) {
        Station station = getRequired(stationId);
        Instant now = timeProvider.now();

        if (active) {
            station.activate(now);
        } else {
            station.deactivate(now);
        }

        return station;
    }

    private static String normalizeCode(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    public static void assertVersion(
            long actual,
            long expected
    ) {
        if (actual != expected) {
            throw new ResourceVersionConflictException(
                    "Station",
                    actual,
                    expected
            );
        }
    }
}