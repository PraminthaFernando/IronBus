package com.lsf.ironbus.train.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "ib_coaches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_train_coach_number",
                        columnNames = {"train_id", "coach_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coach extends BaseEntity {

    private static final int MAX_COACH_NUMBER_LENGTH = 20;

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "coach_number", nullable = false, length = 20)
    private String coachNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_class", nullable = false, length = 30)
    private TravelClass travelClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_mode", nullable = false, length = 20)
    private CoachReservationMode reservationMode;

    @Column(nullable = false)
    private boolean active;

    public Coach(
            UUID id,
            Train train,
            String coachNumber,
            TravelClass travelClass,
            CoachReservationMode reservationMode,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Coach id is required");
        this.train = Objects.requireNonNull(train, "Train is required");
        this.coachNumber = normalizeCoachNumber(coachNumber);
        this.travelClass = Objects.requireNonNull(
                travelClass,
                "Travel class is required"
        );
        this.reservationMode = Objects.requireNonNull(
                reservationMode,
                "Reservation mode is required"
        );
        this.active = true;
    }

    public boolean isReserved() {
        return reservationMode == CoachReservationMode.RESERVED;
    }

    public void deactivate(Instant updatedAt) {
        this.active = false;
        markUpdated(updatedAt);
    }

    private static String normalizeCoachNumber(String coachNumber) {
        if (coachNumber == null || coachNumber.isBlank()) {
            throw new IllegalArgumentException("Coach number is required");
        }

        String normalized = coachNumber
                .trim()
                .toUpperCase(Locale.ROOT);

        if (normalized.length() > MAX_COACH_NUMBER_LENGTH) {
            throw new IllegalArgumentException(
                    "Coach number cannot exceed "
                            + MAX_COACH_NUMBER_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }
}