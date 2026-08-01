package com.lsf.ironbus.booking.domain;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.domain.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @JoinColumn(name = "origin_route_station_id", nullable = false)
    private RouteStation originRouteStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_route_station_id", nullable = false)
    private RouteStation destinationRouteStation;

    @Column(name = "origin_sequence", nullable = false)
    private int originSequence;

    @Column(name = "destination_sequence", nullable = false)
    private int destinationSequence;

    @Column(name = "fare_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal fareAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    public Booking(
            UUID id,
            String reference,
            Journey journey,
            Seat seat,
            RouteStation originRouteStation,
            RouteStation destinationRouteStation,
            int originSequence,
            int destinationSequence,
            BigDecimal fareAmount,
            Currency currency,
            BookingStatus status,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id);
        this.reference = Objects.requireNonNull(reference);
        this.journey = Objects.requireNonNull(journey);
        this.seat = Objects.requireNonNull(seat);
        this.originRouteStation =
                Objects.requireNonNull(originRouteStation);
        this.destinationRouteStation =
                Objects.requireNonNull(destinationRouteStation);
        this.originSequence = originSequence;
        this.destinationSequence = destinationSequence;
        this.fareAmount = Objects.requireNonNull(fareAmount);
        this.currency = Objects.requireNonNull(currency)
                .getCurrencyCode();
        this.status = Objects.requireNonNull(status);
    }
}