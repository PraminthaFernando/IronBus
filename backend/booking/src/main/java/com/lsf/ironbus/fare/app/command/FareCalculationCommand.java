package com.lsf.ironbus.fare.app.command;

import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public record FareCalculationCommand(
        UUID journeyId,
        UUID originStationId,
        UUID destinationStationId,
        TravelClass travelClass
) {
}