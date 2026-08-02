ALTER TABLE ib_bookings
    ADD COLUMN passenger_name VARCHAR(150),
    ADD COLUMN passenger_email VARCHAR(254),
    ADD COLUMN passenger_phone VARCHAR(30),
    ADD COLUMN cancelled_at TIMESTAMPTZ;

UPDATE ib_bookings
SET
    passenger_name = 'Legacy Passenger',
    passenger_email = 'legacy@example.invalid',
    passenger_phone = 'N/A'
WHERE passenger_name IS NULL;

ALTER TABLE ib_bookings
    ALTER COLUMN passenger_name SET NOT NULL,
ALTER COLUMN passenger_email SET NOT NULL,
    ALTER COLUMN passenger_phone SET NOT NULL;

CREATE INDEX idx_bookings_reference
    ON ib_bookings(reference);

CREATE INDEX idx_bookings_status
    ON ib_bookings(status);