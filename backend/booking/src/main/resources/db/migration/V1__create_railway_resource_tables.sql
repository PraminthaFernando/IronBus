CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE ib_stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_station_code UNIQUE (code),
    CONSTRAINT ck_station_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT ck_station_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE ib_routes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL,
    name VARCHAR(200) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_route_code UNIQUE (code),
    CONSTRAINT ck_route_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT ck_route_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE ib_route_stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    route_id UUID NOT NULL REFERENCES routes(id),
    station_id UUID NOT NULL REFERENCES stations(id),
    sequence_number INTEGER NOT NULL,
    distance_from_origin_km NUMERIC(8,2) NOT NULL,
    scheduled_offset_minutes INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_route_station UNIQUE (route_id, station_id),
    CONSTRAINT uk_route_station_sequence UNIQUE (route_id, sequence_number),
    CONSTRAINT ck_route_station_sequence_non_negative
        CHECK (sequence_number >= 0),
    CONSTRAINT ck_route_station_distance_non_negative
        CHECK (distance_from_origin_km >= 0),
    CONSTRAINT ck_route_station_offset_non_negative
        CHECK (scheduled_offset_minutes >= 0)
);

CREATE TABLE ib_trains (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_train_code UNIQUE (code),
    CONSTRAINT ck_train_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT ck_train_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE ib_coaches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    train_id UUID NOT NULL REFERENCES trains(id),
    coach_number VARCHAR(20) NOT NULL,
    travel_class VARCHAR(30) NOT NULL,
    reservation_mode VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_train_coach_number UNIQUE (train_id, coach_number),
    CONSTRAINT ck_coach_number_not_blank CHECK (btrim(coach_number) <> ''),
    CONSTRAINT ck_coach_travel_class CHECK (
        travel_class IN ('FIRST_CLASS', 'SECOND_CLASS', 'THIRD_CLASS')
    ),
    CONSTRAINT ck_coach_reservation_mode CHECK (
        reservation_mode IN ('RESERVED', 'UNRESERVED')
    )
);

CREATE TABLE ib_seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coach_id UUID NOT NULL REFERENCES coaches(id),
    seat_number VARCHAR(20) NOT NULL,
    seat_type VARCHAR(20) NOT NULL,
    row_number INTEGER,
    column_number INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_coach_seat_number UNIQUE (coach_id, seat_number),
    CONSTRAINT ck_seat_number_not_blank CHECK (btrim(seat_number) <> ''),
    CONSTRAINT ck_seat_type CHECK (
       seat_type IN ('WINDOW', 'AISLE', 'MIDDLE', 'OTHER')
       ),
    CONSTRAINT ck_seat_row_positive CHECK (
       row_number IS NULL OR row_number > 0
       ),
    CONSTRAINT ck_seat_column_positive CHECK (
       column_number IS NULL OR column_number > 0
       )
);

CREATE TABLE ib_journeys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    train_id UUID NOT NULL REFERENCES trains(id),
    route_id UUID NOT NULL REFERENCES routes(id),
    departure_time TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_train_departure UNIQUE (train_id, departure_time),
    CONSTRAINT ck_journey_status CHECK (
        status IN (
            'SCHEDULED',
            'BOARDING',
            'DEPARTED',
            'COMPLETED',
            'CANCELLED'
        )
    )
);

CREATE INDEX idx_route_stations_route_sequence
    ON route_stations(route_id, sequence_number);

CREATE INDEX idx_coaches_train
    ON coaches(train_id);

CREATE INDEX idx_seats_coach
    ON seats(coach_id);

CREATE INDEX idx_journeys_route_departure
    ON journeys(route_id, departure_time);

CREATE INDEX idx_journeys_train_departure
    ON journeys(train_id, departure_time);