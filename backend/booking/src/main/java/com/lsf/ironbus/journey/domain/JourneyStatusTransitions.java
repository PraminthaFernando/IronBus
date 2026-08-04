package com.lsf.ironbus.journey.domain;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.exception.InvalidJourneyStatusTransitionException;

public final class JourneyStatusTransitions {

    private JourneyStatusTransitions() {
    }

    public static void assertAllowed(
            JourneyStatus current,
            JourneyStatus target
    ) {
        boolean allowed = switch (current) {
            case SCHEDULED ->
                    target == JourneyStatus.BOARDING
                            || target == JourneyStatus.CANCELLED;

            case BOARDING ->
                    target == JourneyStatus.DEPARTED
                            || target == JourneyStatus.CANCELLED;

            case DEPARTED ->
                    target == JourneyStatus.COMPLETED;

            case COMPLETED, CANCELLED -> false;

            default -> throw new InvalidJourneyStatusTransitionException(current, target);
        };

        if (!allowed) {
            throw new InvalidJourneyStatusTransitionException(
                    current,
                    target
            );
        }
    }
}