package com.lsf.ironbus.journey.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public final class TrainHasNoDeactivatedJourneyException
        extends DomainException {

    public static final String CODE =
            "TRAIN_HAS_NO_DEACTIVATED_JOURNEY";

    private final UUID trainId;

    public TrainHasNoDeactivatedJourneyException(UUID trainId) {
        super(
                CODE,
                "Train "
                        + trainId
                        + " cannot be activated while it has no deactivated journey",
                HttpStatus.CONFLICT
        );

        this.trainId = trainId;
    }

}