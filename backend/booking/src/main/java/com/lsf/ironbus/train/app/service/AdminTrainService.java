package com.lsf.ironbus.train.app.service;

import com.lsf.ironbus.journey.app.service.TrainJourneyLifecycleService;
import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.shared.infra.SystemTimeProvider;
import com.lsf.ironbus.train.app.response.TrainAdminResponse;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.exception.TrainNotFoundException;
import com.lsf.ironbus.train.repository.TrainRepository;
import com.lsf.ironbus.train.web.request.UpdateTrainRequest;
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
public class AdminTrainService {

    private final TrainRepository trainRepository;
    private final SystemTimeProvider timeProvider;
    private final TrainJourneyLifecycleService journeyLifecycleService;

    @Transactional(readOnly = true)
    public TrainAdminResponse getAdminResponse(
            UUID trainId
    ) {
        return trainRepository
                .findAdminById(trainId)
                .map(TrainAdminResponse::from)
                .orElseThrow(() ->
                        new TrainNotFoundException(trainId)
                );
    }

    @Transactional(readOnly = true)
    public Page<TrainAdminResponse> searchAdmin(
            String search,
            Pageable pageable
    ) {
        String normalizedSearch =
                search == null
                        ? ""
                        : search.trim();

        return trainRepository
                .findAdminPage(
                        normalizedSearch,
                        pageable
                )
                .map(TrainAdminResponse::from);
    }

    @Transactional
    public TrainAdminResponse update(
            UUID trainId,
            UpdateTrainRequest request
    ) {
        Train train = trainRepository
                .findByIdWithCoaches(trainId)
                .orElseThrow(() ->
                        new TrainNotFoundException(trainId)
                );

        assertVersion(
                train.getVersion(),
                request.expectedVersion()
        );

        String normalizedCode = request.code()
                .trim()
                .toUpperCase(Locale.ROOT);

        trainRepository
                .findByCodeIgnoreCase(normalizedCode)
                .filter(existing ->
                        !existing.getId().equals(trainId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Train code already exists: "
                                    + normalizedCode
                    );
                });

        train.update(
                normalizedCode,
                request.name().trim(),
                request.active()
        );

        setActive(trainId, request.active());

        trainRepository.flush();

        return TrainAdminResponse.from(train);
    }

    @Transactional
    public TrainAdminResponse setActive(
            UUID trainId,
            boolean active
    ) {
        Instant now = timeProvider.now();
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() ->
                        new TrainNotFoundException(trainId)
                );

        if (active) {
            train.activate(now);

            journeyLifecycleService.resumeFutureJourneys(
                    trainId,
                    now
            );
        } else {
            journeyLifecycleService.assertTrainCanBeDeactivated(
                    trainId,
                    now
            );

            train.deactivate(now);

            journeyLifecycleService.suspendFutureJourneys(
                    trainId,
                    now
            );
        }

        trainRepository.flush();

        return trainRepository.findAdminById(trainId)
                .map(TrainAdminResponse::from)
                .orElseThrow(() ->
                        new TrainNotFoundException(trainId)
                );
    }

    @Transactional(readOnly = true)
    public Train getRequired(
            UUID trainId
    ) {
        return trainRepository.findById(trainId)
                .orElseThrow(() ->
                        new TrainNotFoundException(trainId)
                );
    }

    @Transactional(readOnly = true)
    public void assertExists(UUID trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new TrainNotFoundException(trainId);
        }
    }

    private static void assertVersion(
            long actualVersion,
            long expectedVersion
    ) {
        if (actualVersion != expectedVersion) {
            throw new ResourceVersionConflictException(
                    "Train",
                    actualVersion,
                    expectedVersion
            );
        }
    }
}