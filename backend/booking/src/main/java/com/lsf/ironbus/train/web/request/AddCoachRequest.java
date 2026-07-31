package com.lsf.ironbus.train.web.request;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddCoachRequest(
        @NotBlank
        @Size(max = 20)
        String coachNumber,

        @NotNull
        TravelClass travelClass,

        @NotNull
        CoachReservationMode reservationMode
) {
}