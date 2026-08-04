package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.booking.exception.SeatNotFoundException;
import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.shared.infra.SystemTimeProvider;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.exception.InvalidSeatLayoutException;
import com.lsf.ironbus.train.exception.SeatNotAllowedForUnreservedCoachException;
import com.lsf.ironbus.train.exception.SeatNumberAlreadyExistsException;
import com.lsf.ironbus.train.exception.DuplicateSeatColumnException;
import com.lsf.ironbus.train.repository.SeatRepository;
import com.lsf.ironbus.train.web.request.BulkCreateSeatsRequest;
import com.lsf.ironbus.train.web.request.CreateSeatRequest;
import com.lsf.ironbus.train.web.request.UpdateSeatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSeatService {

    private final SeatRepository seatRepository;
    private final AdminCoachService adminCoachService;
    private final SystemTimeProvider timeProvider;

    @Transactional(readOnly = true)
    public Page<Seat> searchByCoach(
            UUID coachId,
            String search,
            Pageable pageable
    ) {
        adminCoachService.getRequired(coachId);

        String query = search == null
                ? ""
                : search.trim();

        if (query.isEmpty()) {
            return seatRepository.findByCoachId(
                    coachId,
                    pageable
            );
        }

        return seatRepository
                .findByCoachIdAndSeatNumberContainingIgnoreCase(
                        coachId,
                        query,
                        pageable
                );
    }

    @Transactional(readOnly = true)
    public Seat getRequired(UUID seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new SeatNotFoundException(seatId)
                );
    }

    @Transactional
    public Seat create(
            UUID coachId,
            CreateSeatRequest request
    ) {
        Coach coach = adminCoachService.getRequired(coachId);

        assertReservedCoach(coach);

        String seatNumber =
                normalizeSeatNumber(request.seatNumber());

        if (seatRepository
                .existsByCoachIdAndSeatNumberIgnoreCase(
                        coachId,
                        seatNumber
                )) {
            throw new SeatNumberAlreadyExistsException(
                    coachId,
                    seatNumber
            );
        }

        Seat seat = Seat.create(
                coach,
                seatNumber,
                request.seatType(),
                request.rowNumber(),
                request.columnNumber(),
                request.active()
        );

        return seatRepository.save(seat);
    }

    @Transactional
    public Seat update(
            UUID seatId,
            UpdateSeatRequest request
    ) {
        Seat seat = getRequired(seatId);

        assertVersion(
                seat.getVersion(),
                request.expectedVersion()
        );

        assertReservedCoach(seat.getCoach());

        String seatNumber =
                normalizeSeatNumber(request.seatNumber());

        seatRepository
                .findByCoachIdAndSeatNumberIgnoreCase(
                        seat.getCoach().getId(),
                        seatNumber
                )
                .filter(existing ->
                        !existing.getId().equals(seatId)
                )
                .ifPresent(existing -> {
                    throw new SeatNumberAlreadyExistsException(
                            seat.getCoach().getId(),
                            seatNumber
                    );
                });

        seat.update(
                seatNumber,
                request.seatType(),
                request.rowNumber(),
                request.columnNumber(),
                request.active()
        );

        return seat;
    }

    @Transactional
    public Seat setActive(
            UUID seatId,
            boolean active
    ) {
        Seat seat = getRequired(seatId);
        Instant now = timeProvider.now();

        if (active) {
            seat.activate(now);
        } else {
            seat.deactivate(now);
        }

        return seat;
    }

    @Transactional
    public List<Seat> bulkCreate(
            UUID coachId,
            BulkCreateSeatsRequest request
    ) {
        Coach coach = adminCoachService.getRequired(coachId);

        assertReservedCoach(coach);

        List<String> suffixes =
                normalizeSuffixes(
                        request.columnSuffixes()
                );

        validateSuffixes(
                suffixes,
                request.columnSuffixes().size()
        );

        Set<String> existingSeatNumbers =
                seatRepository
                        .findSeatNumbersByCoachId(coachId)
                        .stream()
                        .map(AdminSeatService::normalizeSeatNumber)
                        .collect(Collectors.toSet());

        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= request.rows(); row++) {
            for (
                    int columnIndex = 0;
                    columnIndex < suffixes.size();
                    columnIndex++
            ) {
                String suffix = suffixes.get(columnIndex);
                String seatNumber =
                        normalizeSeatNumber(row + suffix);

                if (!existingSeatNumbers.add(seatNumber)) {
                    throw new SeatNumberAlreadyExistsException(
                            coachId,
                            seatNumber
                    );
                }

                Seat seat = Seat.create(
                        coach,
                        seatNumber,
                        request.seatType(),
                        row,
                        columnIndex + 1,
                        true
                );

                seats.add(seat);
            }
        }

        return seatRepository.saveAll(seats);
    }

    private static void assertReservedCoach(
            Coach coach
    ) {
        if (coach.getReservationMode()
                != CoachReservationMode.RESERVED) {
            throw new SeatNotAllowedForUnreservedCoachException(
                    coach.getId()
            );
        }
    }

    private static List<String> normalizeSuffixes(
            List<String> values
    ) {
        return values.stream()
                .map(value ->
                        value.trim()
                                .toUpperCase(Locale.ROOT)
                )
                .toList();
    }

    private static void validateSuffixes(
            List<String> normalized,
            int originalSize
    ) {
        Set<String> unique = new HashSet<>(normalized);

        if (unique.size() != originalSize) {
            throw new DuplicateSeatColumnException();
        }

        if (normalized.stream().anyMatch(String::isBlank)) {
            throw new InvalidSeatLayoutException(
                    "Seat column suffixes must not be blank"
            );
        }
    }

    private static String normalizeSeatNumber(
            String value
    ) {
        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private static void assertVersion(
            long actual,
            long expected
    ) {
        if (actual != expected) {
            throw new ResourceVersionConflictException(
                    "Seat",
                    actual,
                    expected
            );
        }
    }
}