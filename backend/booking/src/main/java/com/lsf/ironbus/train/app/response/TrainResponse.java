package com.lsf.ironbus.train.app.response;

import com.lsf.ironbus.train.domain.Train;

import java.util.UUID;

public record TrainResponse(
        UUID id,
        String code,
        String name,
        boolean active
) {
    public static TrainResponse from(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getCode(),
                train.getName(),
                train.isActive()
        );
    }
}