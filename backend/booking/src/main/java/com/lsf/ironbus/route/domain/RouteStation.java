package com.lsf.ironbus.route.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.station.domain.Station;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Column(name = "distance_from_origin_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceFromOriginKm;

    @Column(name = "scheduled_offset_minutes", nullable = false)
    private int scheduledOffsetMinutes;

    @Column(nullable = false)
    private boolean active;
}