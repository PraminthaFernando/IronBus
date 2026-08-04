package com.lsf.ironbus.shared.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceVersionConflictException
        extends DomainException {

    private final String resourceType;
    private final long actualVersion;
    private final long expectedVersion;

    public ResourceVersionConflictException(
            String resourceType,
            long actualVersion,
            long expectedVersion
    ) {
        super(
            "VERSION_CONFLICT",
            buildMessage(
                resourceType,
                actualVersion,
                expectedVersion
            ),
            HttpStatus.CONFLICT
        );

        this.resourceType = normalizeResourceType(resourceType);
        this.actualVersion = actualVersion;
        this.expectedVersion = expectedVersion;
    }

    private static String buildMessage(
            String resourceType,
            long actualVersion,
            long expectedVersion
    ) {
        String normalizedType =
                normalizeResourceType(resourceType);

        return "%s was modified by another request. Expected version %d, but the current version is %d."
                .formatted(
                        normalizedType,
                        expectedVersion,
                        actualVersion
                );
    }

    private static String normalizeResourceType(
            String resourceType
    ) {
        if (resourceType == null || resourceType.isBlank()) {
            return "Resource";
        }

        return resourceType.trim();
    }
}