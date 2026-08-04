package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

@Getter
public final class CoachHasSeatsException
        extends DomainException {

    public static final String CODE =
            "COACH_HAS_SEATS";

    private final UUID coachId;

    public CoachHasSeatsException(UUID coachId) {
        super(
            CODE,
            "Coach "
                + Objects.requireNonNull(
                coachId,
                "Coach ID must not be null"
            )
                + " contains seats and cannot be changed to unreserved",
            HttpStatus.CONFLICT
        );

        this.coachId = coachId;
    }

}