package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.app.command.RescheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyAdminResponse;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.UUID;

public interface AdminJourneyService {

    Page<JourneyAdminResponse> search(
            UUID trainId,
            UUID routeId,
            JourneyStatus status,
            LocalDate departureDateFrom,
            LocalDate departureDateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    JourneyAdminResponse getById(UUID journeyId);

    JourneyAdminResponse reschedule(
            RescheduleJourneyCommand command
    );

    JourneyAdminResponse updateStatus(
            UUID journeyId,
            JourneyStatus targetStatus,
            long expectedVersion
    );

    JourneyAdminResponse cancel(
            UUID journeyId,
            long expectedVersion
    );

    JourneyAdminResponse reactivate(
            UUID journeyId,
            long expectedVersion
    );

    void delete(
            UUID journeyId,
            long expectedVersion
    );
}