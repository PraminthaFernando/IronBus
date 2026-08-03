package com.lsf.ironbus.shared.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected DomainException(
            String code,
            String message,
            HttpStatus status
    ) {
        super(Objects.requireNonNull(message, "message must not be null"));
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

}