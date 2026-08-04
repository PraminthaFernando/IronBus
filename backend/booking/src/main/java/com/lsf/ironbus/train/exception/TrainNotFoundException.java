package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

@Getter
public final class TrainNotFoundException
        extends DomainException {

    public static final String CODE = "TRAIN_NOT_FOUND";

    private final UUID trainId;

    public TrainNotFoundException(UUID trainId) {
        super(
            CODE,
            "Train was not found: "
                + Objects.requireNonNull(
                trainId,
                "Train ID must not be null"
            ),
            HttpStatus.NOT_FOUND
        );

        this.trainId = trainId;
    }

}