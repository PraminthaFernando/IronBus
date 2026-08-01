package com.lsf.ironbus.train.app.response;

import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public record CoachResponse(
        UUID id,
        UUID trainId,
        String coachNumber,
        TravelClass travelClass,
        CoachReservationMode reservationMode,
        boolean active
) {
    public static CoachResponse from(Coach coach) {
        return new CoachResponse(
                coach.getId(),
                coach.getTrain().getId(),
                coach.getCoachNumber(),
                coach.getTravelClass(),
                coach.getReservationMode(),
                coach.isActive()
        );
    }
}