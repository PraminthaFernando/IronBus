package com.lsf.ironbus.booking.domain;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.booking.exception.BookingAlreadyCancelledException;
import com.lsf.ironbus.booking.exception.BookingNotCancellableException;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.domain.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ib_bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "origin_route_station_id",
            nullable = false
    )
    private RouteStation originRouteStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "destination_route_station_id",
            nullable = false
    )
    private RouteStation destinationRouteStation;

    @Column(name = "origin_sequence", nullable = false)
    private int originSequence;

    @Column(name = "destination_sequence", nullable = false)
    private int destinationSequence;

    @Column(name = "passenger_name", nullable = false, length = 150)
    private String passengerName;

    @Column(name = "passenger_email", nullable = false, length = 254)
    private String passengerEmail;

    @Column(name = "passenger_phone", nullable = false, length = 30)
    private String passengerPhone;

    @Column(
            name = "fare_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal fareAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    public Booking(
            UUID id,
            BookingReference reference,
            Journey journey,
            Seat seat,
            RouteStation originRouteStation,
            RouteStation destinationRouteStation,
            int originSequence,
            int destinationSequence,
            PassengerDetails passenger,
            BigDecimal fareAmount,
            Currency currency,
            Instant createdAt
    ) {
        super(createdAt);

        if (originSequence >= destinationSequence) {
            throw new IllegalArgumentException(
                    "Origin sequence must be before destination sequence"
            );
        }

        this.id = Objects.requireNonNull(id, "Booking id is required");
        this.reference = Objects
                .requireNonNull(reference, "Reference is required")
                .value();
        this.journey = Objects.requireNonNull(
                journey,
                "Journey is required"
        );
        this.seat = Objects.requireNonNull(
                seat,
                "Seat is required"
        );
        this.originRouteStation = Objects.requireNonNull(
                originRouteStation,
                "Origin route station is required"
        );
        this.destinationRouteStation = Objects.requireNonNull(
                destinationRouteStation,
                "Destination route station is required"
        );
        this.originSequence = originSequence;
        this.destinationSequence = destinationSequence;

        PassengerDetails details = Objects.requireNonNull(
                passenger,
                "Passenger details are required"
        );

        this.passengerName = details.name();
        this.passengerEmail = details.email();
        this.passengerPhone = details.phone();

        this.fareAmount = Objects
                .requireNonNull(fareAmount, "Fare is required")
                .setScale(2, RoundingMode.HALF_UP);

        if (this.fareAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Fare cannot be negative"
            );
        }

        this.currency = Objects
                .requireNonNull(currency, "Currency is required")
                .getCurrencyCode();

        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel(Instant now) {
        if (status == BookingStatus.CANCELLED) {
            throw new BookingAlreadyCancelledException(reference);
        }

        if (status != BookingStatus.CONFIRMED) {
            throw new BookingNotCancellableException(reference, status);
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = Objects.requireNonNull(now);
        markUpdated(now);
    }
}