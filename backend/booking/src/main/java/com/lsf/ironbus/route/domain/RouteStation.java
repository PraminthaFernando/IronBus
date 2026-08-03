package com.lsf.ironbus.route.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.station.domain.Station;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "Ib_route_stations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_route_station",
                        columnNames = {"route_id", "station_id"}
                ),
                @UniqueConstraint(
                        name = "uk_route_station_sequence",
                        columnNames = {"route_id", "sequence_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStation extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(
            name = "distance_from_origin_km",
            nullable = false,
            precision = 8,
            scale = 2
    )
    private BigDecimal distanceFromOriginKm;

    @Column(name = "scheduled_offset_minutes", nullable = false)
    private int scheduledOffsetMinutes;

    @Column(nullable = false)
    private boolean active;

    public RouteStation(
            UUID id,
            Route route,
            Station station,
            int sequenceNumber,
            BigDecimal distanceFromOriginKm,
            int scheduledOffsetMinutes,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Route station id is required");
        this.route = Objects.requireNonNull(route, "Route is required");
        this.station = Objects.requireNonNull(station, "Station is required");
        this.sequenceNumber = validateSequence(sequenceNumber);
        this.distanceFromOriginKm = validateDistance(distanceFromOriginKm);
        this.scheduledOffsetMinutes = validateOffset(scheduledOffsetMinutes);
        this.active = true;

        validateFirstStation();
    }

    public static RouteStation create(
            Route route,
            Station station,
            int sequenceNumber,
            BigDecimal distanceFromOriginKm,
            int scheduledOffsetMinutes
    ) {
        Objects.requireNonNull(
                route,
                "Route must not be null"
        );

        Objects.requireNonNull(
                station,
                "Station must not be null"
        );

        Objects.requireNonNull(
                distanceFromOriginKm,
                "Distance from origin must not be null"
        );

        if (sequenceNumber < 0) {
            throw new IllegalArgumentException(
                    "Route station sequence number must not be negative"
            );
        }

        if (distanceFromOriginKm.signum() < 0) {
            throw new IllegalArgumentException(
                    "Distance from origin must not be negative"
            );
        }

        if (scheduledOffsetMinutes < 0) {
            throw new IllegalArgumentException(
                    "Scheduled offset minutes must not be negative"
            );
        }

        RouteStation routeStation =
                new RouteStation();

        routeStation.route = route;
        routeStation.station = station;
        routeStation.sequenceNumber =
                sequenceNumber;
        routeStation.distanceFromOriginKm =
                distanceFromOriginKm.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );
        routeStation.scheduledOffsetMinutes =
                scheduledOffsetMinutes;
        routeStation.active = true;

        return routeStation;
    }

    private void validateFirstStation() {
        if (sequenceNumber == 0) {
            if (distanceFromOriginKm.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException(
                        "The first station distance must be zero"
                );
            }

            if (scheduledOffsetMinutes != 0) {
                throw new IllegalArgumentException(
                        "The first station scheduled offset must be zero"
                );
            }
        }
    }

    private static int validateSequence(int sequenceNumber) {
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException(
                    "Route station sequence cannot be negative"
            );
        }

        return sequenceNumber;
    }

    private static BigDecimal validateDistance(BigDecimal distance) {
        if (distance == null) {
            throw new IllegalArgumentException(
                    "Distance from origin is required"
            );
        }

        if (distance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Distance from origin cannot be negative"
            );
        }

        return distance.setScale(2);
    }

    private static int validateOffset(int offsetMinutes) {
        if (offsetMinutes < 0) {
            throw new IllegalArgumentException(
                    "Scheduled offset cannot be negative"
            );
        }

        return offsetMinutes;
    }
}