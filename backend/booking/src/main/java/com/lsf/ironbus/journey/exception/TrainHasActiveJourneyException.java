package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public final class TrainHasActiveJourneyException
        extends DomainException {

    public static final String CODE =
            "TRAIN_HAS_ACTIVE_JOURNEY";

    private final UUID trainId;

    public TrainHasActiveJourneyException(UUID trainId) {
        super(
                CODE,
                "Train "
                        + trainId
                        + " cannot be deactivated while it has an active journey",
                HttpStatus.CONFLICT
        );

        this.trainId = trainId;
    }

}