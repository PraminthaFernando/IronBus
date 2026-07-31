package com.lsf.ironbus.shared.error;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(
            String resourceName,
            Object identifier
    ) {
        super(
                resourceName.toUpperCase() + "_NOT_FOUND",
                resourceName + " was not found: " + identifier
        );
    }
}