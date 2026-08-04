package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.app.command.RescheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyAdminMapper;
import com.lsf.ironbus.journey.app.response.JourneyAdminResponse;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.exception.JourneyDepartureAlreadyScheduledException;
import com.lsf.ironbus.journey.exception.JourneyHasBookingsException;
import com.lsf.ironbus.journey.exception.JourneyNotFoundException;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.shared.error.ResourceVersionConflictException;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static com.lsf.ironbus.journey.repository.JourneySpecifications.*;

@Service
@RequiredArgsConstructor
public class DefaultAdminJourneyService
        implements AdminJourneyService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "departureTime",
            "status",
            "createdAt",
            "updatedAt"
    );

    private final JourneyRepository journeyRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final JourneyAdminMapper journeyAdminMapper;
    private final JourneyBookingGuard journeyBookingGuard;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public Page<JourneyAdminResponse> search(
            UUID trainId,
            UUID routeId,
            JourneyStatus status,
            LocalDate departureDateFrom,
            LocalDate departureDateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        validateDateRange(
                departureDateFrom,
                departureDateTo
        );

        String safeSortField = ALLOWED_SORT_FIELDS.contains(sortBy)
                ? sortBy
                : "departureTime";

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, safeSortField)
        );

        Specification<Journey> specification =
                Specification.where(hasTrainId(trainId))
                        .and(hasRouteId(routeId))
                        .and(hasStatus(status))
                        .and(departureOnOrAfter(departureDateFrom))
                        .and(departureBeforeDayAfter(departureDateTo));

        return journeyRepository
                .findAll(specification, pageable)
                .map(journeyAdminMapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JourneyAdminResponse getById(UUID journeyId) {
        Journey journey = getRequired(journeyId);
        return journeyAdminMapper.toAdminResponse(journey);
    }

    @Override
    @Transactional
    public JourneyAdminResponse reschedule(
            RescheduleJourneyCommand command
    ) {
        Journey journey = getRequired(command.journeyId());

        assertVersion(
                journey,
                command.expectedVersion()
        );

        journeyBookingGuard.assertCanReschedule(journey.getId());

        Train train = trainRepository
                .findById(command.trainId())
                .orElseThrow(() ->
                        new com.lsf.ironbus.train.exception
                                .TrainNotFoundException(command.trainId())
                );

        Route route = routeRepository
                .findById(command.routeId())
                .orElseThrow(() ->
                        new com.lsf.ironbus.shared.error
                                .ResourceNotFoundException(
                                "Route",
                                command.routeId()
                        )
                );

        assertTrainDepartureIsAvailable(
                command.journeyId(),
                command.trainId(),
                command.departureTime()
        );

        journey.reschedule(
                train,
                route,
                command.departureTime(),
                clock.instant()
        );

        return journeyAdminMapper.toAdminResponse(journey);
    }

    @Override
    @Transactional
    public JourneyAdminResponse updateStatus(
            UUID journeyId,
            JourneyStatus targetStatus,
            long expectedVersion
    ) {
        Journey journey = getRequired(journeyId);

        assertVersion(journey, expectedVersion);

        switch (targetStatus) {
            case BOARDING -> journey.startBoarding(clock.instant());
            case DEPARTED -> journey.markDeparted(clock.instant());
            case COMPLETED -> journey.complete(clock.instant());

            case CANCELLED ->
                    throw new IllegalArgumentException(
                            "Use the dedicated cancellation endpoint"
                    );

            case SCHEDULED ->
                    throw new IllegalArgumentException(
                            "Use the dedicated reactivation endpoint"
                    );
        }

        return journeyAdminMapper.toAdminResponse(journey);
    }

    @Override
    @Transactional
    public JourneyAdminResponse cancel(
            UUID journeyId,
            long expectedVersion
    ) {
        Journey journey = getRequired(journeyId);

        assertVersion(journey, expectedVersion);

        journeyBookingGuard.assertCanCancel(journeyId);

        journey.cancel(clock.instant());

        return journeyAdminMapper.toAdminResponse(journey);
    }

    @Override
    @Transactional
    public JourneyAdminResponse reactivate(
            UUID journeyId,
            long expectedVersion
    ) {
        Journey journey = getRequired(journeyId);

        assertVersion(journey, expectedVersion);

        journeyBookingGuard.assertCanReactivate(journeyId);

        assertTrainDepartureIsAvailable(
                journeyId,
                journey.getTrain().getId(),
                journey.getDepartureTime()
        );

        journey.reactivate(clock.instant());

        return journeyAdminMapper.toAdminResponse(journey);
    }

    @Override
    @Transactional
    public void delete(
            UUID journeyId,
            long expectedVersion
    ) {
        Journey journey = getRequired(journeyId);

        assertVersion(journey, expectedVersion);

        if (journeyBookingGuard.hasBookings(journeyId)) {
            throw new JourneyHasBookingsException(journeyId);
        }

        journey.assertCanDelete(clock.instant());

        journeyRepository.delete(journey);
    }

    private Journey getRequired(UUID journeyId) {
        return journeyRepository
                .findById(journeyId)
                .orElseThrow(() ->
                        new JourneyNotFoundException(journeyId)
                );
    }

    private void assertTrainDepartureIsAvailable(
            UUID journeyId,
            UUID trainId,
            Instant departureTime
    ) {
        boolean exists =
                journeyRepository
                        .existsByTrainIdAndDepartureTimeAndIdNot(
                                trainId,
                                departureTime,
                                journeyId
                        );

        if (exists) {
            throw new JourneyDepartureAlreadyScheduledException(
                    trainId,
                    departureTime
            );
        }
    }

    private static void assertVersion(
            Journey journey,
            long expectedVersion
    ) {
        if (journey.getVersion() != expectedVersion) {
            throw new ResourceVersionConflictException(
                    "Journey",
                    journey.getVersion(),
                    expectedVersion
            );
        }
    }

    private static void validateDateRange(
            LocalDate from,
            LocalDate to
    ) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Departure date from cannot be after departure date to"
            );
        }
    }
}