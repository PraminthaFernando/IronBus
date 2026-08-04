package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.shared.infra.SystemTimeProvider;
import com.lsf.ironbus.train.app.response.CoachAdminResponse;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.exception.CoachHasSeatsException;
import com.lsf.ironbus.train.exception.CoachNotFoundException;
import com.lsf.ironbus.train.exception.CoachNumberAlreadyExistsException;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.repository.CoachRepository;
import com.lsf.ironbus.train.web.request.UpdateCoachRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCoachService {

    private final CoachRepository coachRepository;
    private final AdminTrainService adminTrainService;
    private final SystemTimeProvider timeProvider;

    @Transactional(readOnly = true)
    public Page<CoachAdminResponse> listAdminByTrain(
            UUID trainId,
            Pageable pageable
    ) {
        adminTrainService.assertExists(trainId);

        return coachRepository
                .findAdminPageByTrainId(
                        trainId,
                        pageable
                )
                .map(CoachAdminResponse::from);
    }

    @Transactional(readOnly = true)
    public CoachAdminResponse getAdminResponse(
            UUID coachId
    ) {
        Coach coach = coachRepository
                .findByIdWithSeats(coachId)
                .orElseThrow(() ->
                        new CoachNotFoundException(coachId)
                );

        return CoachAdminResponse.from(coach);
    }

    @Transactional(readOnly = true)
    public Coach getRequired(
            UUID coachId
    ) {
        return coachRepository.findById(coachId)
                .orElseThrow(() ->
                        new CoachNotFoundException(coachId)
                );
    }

    @Transactional
    public CoachAdminResponse update(
            UUID coachId,
            UpdateCoachRequest request
    ) {
        Coach coach = coachRepository
                .findByIdWithSeats(coachId)
                .orElseThrow(() ->
                        new CoachNotFoundException(coachId)
                );

        assertVersion(
                coach.getVersion(),
                request.expectedVersion()
        );

        String coachNumber = request.coachNumber()
                .trim()
                .toUpperCase(Locale.ROOT);

        coachRepository
                .findByTrainIdAndCoachNumberIgnoreCase(
                        coach.getTrain().getId(),
                        coachNumber
                )
                .filter(existing ->
                        !existing.getId().equals(coachId)
                )
                .ifPresent(existing -> {
                    throw new CoachNumberAlreadyExistsException(
                            coach.getTrain().getId(),
                            coachNumber
                    );
                });

        boolean changingToUnreserved =
                coach.getReservationMode()
                        == CoachReservationMode.RESERVED
                        && request.reservationMode()
                        == CoachReservationMode.UNRESERVED;

        if (changingToUnreserved
                && !coach.getSeats().isEmpty()) {
            throw new CoachHasSeatsException(
                    coach.getId()
            );
        }

        coach.update(
                coachNumber,
                request.travelClass(),
                request.reservationMode(),
                request.active()
        );

        coachRepository.flush();

        return CoachAdminResponse.from(coach);
    }

    @Transactional
    public CoachAdminResponse setActive(
            UUID coachId,
            boolean active
    ) {
        Instant now = timeProvider.now();
        Coach coach = coachRepository
                .findByIdWithSeats(coachId)
                .orElseThrow(() ->
                        new CoachNotFoundException(coachId)
                );

        if (active) {
            coach.activate(now);
        } else {
            coach.deactivate(now);
        }

        coachRepository.flush();

        return CoachAdminResponse.from(coach);
    }

    private static void assertVersion(
            long actualVersion,
            long expectedVersion
    ) {
        if (actualVersion != expectedVersion) {
            throw new ResourceVersionConflictException(
                    "Coach",
                    actualVersion,
                    expectedVersion
            );
        }
    }
}