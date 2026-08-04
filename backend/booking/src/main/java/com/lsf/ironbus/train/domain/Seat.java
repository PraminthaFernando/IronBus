package com.lsf.ironbus.train.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.enums.SeatType;
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
        name = "ib_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coach_seat_number",
                        columnNames = {"coach_id", "seat_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    private static final int MAX_SEAT_NUMBER_LENGTH = 20;

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    private SeatType seatType;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "column_number")
    private Integer columnNumber;

    @Column(nullable = false)
    private boolean active;

    public Seat(
            UUID id,
            Coach coach,
            String seatNumber,
            SeatType seatType,
            Integer rowNumber,
            Integer columnNumber,
            Instant createdAt,
            boolean active
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Seat id is required");
        this.coach = Objects.requireNonNull(coach, "Coach is required");

        if (!coach.isReserved()) {
            throw new IllegalArgumentException(
                    "Individual seats can only be added to reserved coaches"
            );
        }

        this.seatNumber = normalizeSeatNumber(seatNumber);
        this.seatType = Objects.requireNonNull(
                seatType,
                "Seat type is required"
        );
        this.rowNumber = validatePositiveNullable(
                rowNumber,
                "Row number"
        );
        this.columnNumber = validatePositiveNullable(
                columnNumber,
                "Column number"
        );
        this.active = active;
    }

    public static Seat create(
        Coach coach,
        String seatNumber,
        SeatType seatType,
        Integer rowNumber,
        Integer columnNumber,
        boolean active
    ) {
        Seat seat = new Seat();
        seat.coach = Objects.requireNonNull(coach, "Coach is required");

        if (!coach.isReserved()) {
            throw new IllegalArgumentException(
                    "Individual seats can only be added to reserved coaches"
            );
        }

        seat.seatNumber = normalizeSeatNumber(seatNumber);
        seat.seatType = Objects.requireNonNull(
                seatType,
                "Seat type is required"
        );
        seat.rowNumber = validatePositiveNullable(
                rowNumber,
                "Row number"
        );
        seat.columnNumber = validatePositiveNullable(
                columnNumber,
                "Column number"
        );
        seat.active = active;
        return seat;
    }

    public void update(
        String seatNumber,
        SeatType seatType,
        Integer rowNumber,
        Integer columnNumber,
        boolean active
    ) {
        this.seatNumber = normalizeSeatNumber(seatNumber);
        this.seatType = Objects.requireNonNull(
                seatType,
                "Seat type is required"
        );
        this.rowNumber = validatePositiveNullable(
                rowNumber,
                "Row number"
        );
        this.columnNumber = validatePositiveNullable(
                columnNumber,
                "Column number"
        );
        this.active = active;
    }

    public void deactivate(Instant updatedAt) {
        this.active = false;
        markUpdated(updatedAt);
    }

    public void activate(Instant updatedAt) {
        this.active = true;
        markUpdated(updatedAt);
    }

    private static String normalizeSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException("Seat number is required");
        }

        String normalized = seatNumber
                .trim()
                .toUpperCase(Locale.ROOT);

        if (normalized.length() > MAX_SEAT_NUMBER_LENGTH) {
            throw new IllegalArgumentException(
                    "Seat number cannot exceed "
                            + MAX_SEAT_NUMBER_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }

    private static Integer validatePositiveNullable(
            Integer value,
            String fieldName
    ) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive"
            );
        }

        return value;
    }
}