package com.lsf.ironbus.train.app.response;

import com.lsf.ironbus.train.app.projection.TrainAdminProjection;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.CoachReservationMode;

import java.util.UUID;

public record TrainAdminResponse(
        UUID id,
        String code,
        String name,
        boolean active,
        long coachCount,
        long reservedCoachCount,
        long seatCount,
        long version
) {

    public static TrainAdminResponse from(
            Train train
    ) {
        long coachCount =
                train.getCoaches().size();

        long reservedCoachCount =
                train.getCoaches()
                        .stream()
                        .filter(coach ->
                                coach.getReservationMode()
                                        == CoachReservationMode.RESERVED
                        )
                        .count();

        long seatCount =
                train.getCoaches()
                        .stream()
                        .mapToLong(coach ->
                                coach.getSeats().size()
                        )
                        .sum();

        return new TrainAdminResponse(
                train.getId(),
                train.getCode(),
                train.getName(),
                train.isActive(),
                coachCount,
                reservedCoachCount,
                seatCount,
                train.getVersion()
        );
    }

    public static TrainAdminResponse from(
            TrainAdminProjection projection
    ) {
        return new TrainAdminResponse(
                projection.getId(),
                projection.getCode(),
                projection.getName(),
                projection.getActive(),
                projection.getCoachCount(),
                projection.getReservedCoachCount(),
                projection.getSeatCount(),
                projection.getVersion()
        );
    }
}