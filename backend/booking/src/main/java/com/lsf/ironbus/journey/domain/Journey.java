package com.lsf.ironbus.journey.domain;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.domain.Train;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ib_journeys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_train_departure",
                        columnNames = {"train_id", "departure_time"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journey extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JourneyStatus status;
}