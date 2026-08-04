package com.lsf.ironbus.train.web.request;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCoachRequest(
        @NotBlank
        String coachNumber,

        @NotNull
        TravelClass travelClass,

        @NotNull
        CoachReservationMode reservationMode,

        boolean active,

        @NotNull
        @PositiveOrZero
        Long expectedVersion
) {
}