package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

@Getter
public final class CoachNotFoundException
        extends DomainException {

    public static final String CODE =
            "COACH_NOT_FOUND";

    private final UUID coachId;

    public CoachNotFoundException(UUID coachId) {
        super(
                CODE,
                "Coach was not found: "
                        + Objects.requireNonNull(
                        coachId,
                        "Coach ID must not be null"
                ),
                HttpStatus.NOT_FOUND
        );

        this.coachId = coachId;
    }

}
