package com.lsf.ironbus.booking.domain;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.train.domain.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "ib_booking_segments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_journey_seat_segment",
                        columnNames = {
                                "journey_id",
                                "seat_id",
                                "segment_sequence"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingSegment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "segment_sequence", nullable = false)
    private int segmentSequence;

    public BookingSegment(
            UUID id,
            Booking booking,
            Journey journey,
            Seat seat,
            int segmentSequence
    ) {
        if (segmentSequence < 0) {
            throw new IllegalArgumentException(
                    "Segment sequence cannot be negative"
            );
        }

        this.id = Objects.requireNonNull(id);
        this.booking = Objects.requireNonNull(booking);
        this.journey = Objects.requireNonNull(journey);
        this.seat = Objects.requireNonNull(seat);
        this.segmentSequence = segmentSequence;
    }
}