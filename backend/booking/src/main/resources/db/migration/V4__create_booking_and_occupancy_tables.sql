CREATE TABLE ib_bookings (
    id UUID PRIMARY KEY,
    reference VARCHAR(30) NOT NULL,
    journey_id UUID NOT NULL,
    seat_id UUID NOT NULL,
    origin_route_station_id UUID NOT NULL,
    destination_route_station_id UUID NOT NULL,
    origin_sequence INTEGER NOT NULL,
    destination_sequence INTEGER NOT NULL,
    fare_amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_booking_reference
        UNIQUE (reference),

    CONSTRAINT fk_booking_journey
        FOREIGN KEY (journey_id)
            REFERENCES ib_journeys(id),

    CONSTRAINT fk_booking_seat
        FOREIGN KEY (seat_id)
            REFERENCES ib_seats(id),

    CONSTRAINT fk_booking_origin_route_station
        FOREIGN KEY (origin_route_station_id)
            REFERENCES ib_route_stations(id),

    CONSTRAINT fk_booking_destination_route_station
        FOREIGN KEY (destination_route_station_id)
            REFERENCES ib_route_stations(id),

    CONSTRAINT ck_booking_sequences
        CHECK (origin_sequence < destination_sequence),

    CONSTRAINT ck_booking_fare_non_negative
        CHECK (fare_amount >= 0),

    CONSTRAINT ck_booking_currency
        CHECK (char_length(currency) = 3),

    CONSTRAINT ck_booking_status
        CHECK (
            status IN (
                'PENDING',
                'CONFIRMED',
                'CANCELLED',
                'EXPIRED'
            )
        )
);

CREATE TABLE ib_booking_segments (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    journey_id UUID NOT NULL,
    seat_id UUID NOT NULL,
    segment_sequence INTEGER NOT NULL,

    CONSTRAINT fk_booking_segment_booking
        FOREIGN KEY (booking_id)
        REFERENCES ib_bookings(id),

    CONSTRAINT fk_booking_segment_journey
        FOREIGN KEY (journey_id)
        REFERENCES ib_journeys(id),

    CONSTRAINT fk_booking_segment_seat
        FOREIGN KEY (seat_id)
        REFERENCES ib_seats(id),

    CONSTRAINT uk_journey_seat_segment
        UNIQUE (
            journey_id,
            seat_id,
            segment_sequence
        ),

    CONSTRAINT ck_booking_segment_sequence
        CHECK (segment_sequence >= 0)
);

CREATE INDEX idx_booking_segments_journey_segment_seat
    ON ib_booking_segments (
            journey_id,
            segment_sequence,
            seat_id
        );

CREATE INDEX idx_booking_segments_journey_seat
    ON ib_booking_segments (
            journey_id,
            seat_id
        );

CREATE INDEX idx_bookings_journey
    ON ib_bookings(journey_id);

CREATE INDEX idx_bookings_seat
    ON ib_bookings(seat_id);