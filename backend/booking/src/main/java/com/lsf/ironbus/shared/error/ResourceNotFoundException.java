package com.lsf.ironbus.shared.error;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(
            String resourceName,
            Object identifier
    ) {
        super(
                resourceName.toUpperCase() + "_NOT_FOUND",
                resourceName + " was not found: " + identifier,
                HttpStatus.NOT_FOUND
        );
    }
}