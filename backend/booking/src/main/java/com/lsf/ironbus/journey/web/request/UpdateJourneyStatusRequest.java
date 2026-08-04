package com.lsf.ironbus.journey.web.request;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateJourneyStatusRequest(

        @NotNull(message = "Journey status is required")
        JourneyStatus status,

        @PositiveOrZero(message = "Expected version cannot be negative")
        long expectedVersion

) {
}