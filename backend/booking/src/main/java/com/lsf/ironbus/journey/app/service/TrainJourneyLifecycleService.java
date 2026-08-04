package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.exception.TrainHasActiveJourneyException;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainJourneyLifecycleService {

    private final JourneyRepository journeyRepository;

    public void assertTrainCanBeDeactivated(
            UUID trainId,
            Instant now
    ) {
        boolean hasActiveJourney =
                journeyRepository.existsByTrainIdAndStatusIn(
                        trainId,
                        List.of(
                                JourneyStatus.BOARDING,
                                JourneyStatus.DEPARTED
                        )
                );

        if (hasActiveJourney) {
            throw new TrainHasActiveJourneyException(trainId);
        }
    }

    @Transactional
    public void suspendFutureJourneys(
            UUID trainId,
            Instant now
    ) {
        List<Journey> journeys =
                journeyRepository
                        .findAllByTrainIdAndDepartureTimeAfterAndStatus(
                                trainId,
                                now,
                                JourneyStatus.SCHEDULED
                        );

        journeys.forEach(journey ->
                journey.suspendBecauseTrainDeactivated(now)
        );

    }

    @Transactional
    public void resumeFutureJourneys(
            UUID trainId,
            Instant now
    ) {
        List<Journey> journeys =
                journeyRepository
                        .findAllByTrainIdAndDepartureTimeAfterAndStatus(
                                trainId,
                                now,
                                JourneyStatus.SUSPENDED
                        );

        journeys.forEach(journey ->
                journey.resumeAfterTrainReactivated(now)
        );

    }
}