package com.lsf.ironbus.train.app.command;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public record AddCoachCommand(
        UUID trainId,
        String coachNumber,
        TravelClass travelClass,
        CoachReservationMode reservationMode
) {
}