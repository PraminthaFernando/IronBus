package com.lsf.ironbus.train.app.response;

import com.lsf.ironbus.train.app.projection.CoachAdminProjection;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public record CoachAdminResponse(
        UUID id,
        UUID trainId,
        String coachNumber,
        TravelClass travelClass,
        CoachReservationMode reservationMode,
        boolean active,
        long seatCount,
        long version
) {

    public static CoachAdminResponse from(
            Coach coach
    ) {
        return new CoachAdminResponse(
                coach.getId(),
                coach.getTrain().getId(),
                coach.getCoachNumber(),
                coach.getTravelClass(),
                coach.getReservationMode(),
                coach.isActive(),
                coach.getSeats().size(),
                coach.getVersion()
        );
    }

    public static CoachAdminResponse from(
            CoachAdminProjection projection
    ) {
        return new CoachAdminResponse(
                projection.getId(),
                projection.getTrainId(),
                projection.getCoachNumber(),
                projection.getTravelClass(),
                projection.getReservationMode(),
                projection.getActive(),
                projection.getSeatCount(),
                projection.getVersion()
        );
    }
}