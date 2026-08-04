package com.lsf.ironbus.journey.repository;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class JourneySpecifications {

    private JourneySpecifications() {
    }

    public static Specification<Journey> hasTrainId(UUID trainId) {
        return (root, query, builder) ->
                trainId == null
                        ? builder.conjunction()
                        : builder.equal(
                        root.get("train").get("id"),
                        trainId
                );
    }

    public static Specification<Journey> hasRouteId(UUID routeId) {
        return (root, query, builder) ->
                routeId == null
                        ? builder.conjunction()
                        : builder.equal(
                        root.get("route").get("id"),
                        routeId
                );
    }

    public static Specification<Journey> hasStatus(
            JourneyStatus status
    ) {
        return (root, query, builder) ->
                status == null
                        ? builder.conjunction()
                        : builder.equal(
                        root.get("status"),
                        status
                );
    }

    public static Specification<Journey> departureOnOrAfter(
            LocalDate date
    ) {
        return (root, query, builder) -> {
            if (date == null) {
                return builder.conjunction();
            }

            OffsetDateTime start = date
                    .atStartOfDay()
                    .atOffset(ZoneOffset.UTC);

            return builder.greaterThanOrEqualTo(
                    root.get("departureTime"),
                    start
            );
        };
    }

    public static Specification<Journey> departureBeforeDayAfter(
            LocalDate date
    ) {
        return (root, query, builder) -> {
            if (date == null) {
                return builder.conjunction();
            }

            OffsetDateTime exclusiveEnd = date
                    .plusDays(1)
                    .atStartOfDay()
                    .atOffset(ZoneOffset.UTC);

            return builder.lessThan(
                    root.get("departureTime"),
                    exclusiveEnd
            );
        };
    }
}