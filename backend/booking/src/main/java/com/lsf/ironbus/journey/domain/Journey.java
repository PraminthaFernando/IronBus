package com.lsf.ironbus.journey.domain;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.exception.InvalidJourneyDepartureTimeException;
import com.lsf.ironbus.journey.exception.JourneyNotDeletableException;
import com.lsf.ironbus.journey.exception.JourneyNotReschedulableException;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.domain.Train;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
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

    public Journey(
            UUID id,
            Train train,
            Route route,
            Instant departureTime,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Journey id is required");
        this.train = Objects.requireNonNull(train, "Train is required");
        this.route = Objects.requireNonNull(route, "Route is required");
        this.departureTime = Objects.requireNonNull(
                departureTime,
                "Departure time is required"
        );
        this.status = JourneyStatus.SCHEDULED;
    }

    public void reactivate(Instant updatedAt) {
        this.status = JourneyStatus.SCHEDULED;
        markUpdated(updatedAt);
    }

    public boolean isBookable() {
        return status == JourneyStatus.SCHEDULED
                || status == JourneyStatus.BOARDING;
    }

    public void cancel(Instant updatedAt) {
        if (status == JourneyStatus.COMPLETED) {
            throw new IllegalStateException(
                    "A completed journey cannot be cancelled"
            );
        }

        status = JourneyStatus.CANCELLED;
        markUpdated(updatedAt);
    }

    public void complete(Instant updatedAt) {
        markCompleted(updatedAt);
    }

    public void startBoarding(Instant updatedAt) {
        markBoarding(updatedAt);
    }

    public void reschedule(
            Train train,
            Route route,
            Instant departureTime,
            Instant now
    ) {
        if (train == null) {
            throw new IllegalArgumentException(
                    "Train is required"
            );
        }

        if (route == null) {
            throw new IllegalArgumentException(
                    "Route is required"
            );
        }

        if (departureTime == null) {
            throw new IllegalArgumentException(
                    "Departure time is required"
            );
        }

        if (now == null) {
            throw new IllegalArgumentException(
                    "Current time is required"
            );
        }

        if (status != JourneyStatus.SCHEDULED) {
            throw new JourneyNotReschedulableException(
                    id,
                    status
            );
        }

        if (!departureTime.isAfter(now)) {
            throw new InvalidJourneyDepartureTimeException(
                    departureTime
            );
        }

        this.train = train;
        this.route = route;
        this.departureTime = departureTime;
        markUpdated(now);
    }

    public void suspendBecauseTrainDeactivated(
            Instant occurredAt
    ) {
        if (status != JourneyStatus.SCHEDULED) {
            return;
        }

        this.status = JourneyStatus.SUSPENDED;
        markUpdated(occurredAt);
    }

    public void resumeAfterTrainReactivated(
            Instant occurredAt
    ) {
        if (status != JourneyStatus.SUSPENDED) {
            return;
        }

        this.status = JourneyStatus.SCHEDULED;
        markUpdated(occurredAt);
    }

    public void markBoarding(Instant updatedAt) {
        requireStatus(JourneyStatus.SCHEDULED);
        status = JourneyStatus.BOARDING;
        markUpdated(updatedAt);
    }

    public void markDeparted(Instant updatedAt) {
        requireStatus(JourneyStatus.BOARDING);
        status = JourneyStatus.DEPARTED;
        markUpdated(updatedAt);
    }

    public void markCompleted(Instant updatedAt) {
        requireStatus(JourneyStatus.DEPARTED);
        status = JourneyStatus.COMPLETED;
        markUpdated(updatedAt);
    }

    private void requireStatus(JourneyStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Journey must be " + expected
                            + " but is currently " + status
            );
        }
    }

    public void assertCanDelete(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException(
                    "Current time is required"
            );
        }

        if (status == JourneyStatus.BOARDING) {
            throw new JourneyNotDeletableException(
                    id,
                    status,
                    "boarding has already started"
            );
        }

        if (status == JourneyStatus.DEPARTED) {
            throw new JourneyNotDeletableException(
                    id,
                    status,
                    "the journey has already departed"
            );
        }

        if (status == JourneyStatus.COMPLETED) {
            throw new JourneyNotDeletableException(
                    id,
                    status,
                    "completed journeys must be retained for history"
            );
        }

        if (!departureTime.isAfter(now)) {
            throw new JourneyNotDeletableException(
                    id,
                    status,
                    "the departure time has already passed"
            );
        }
    }
}