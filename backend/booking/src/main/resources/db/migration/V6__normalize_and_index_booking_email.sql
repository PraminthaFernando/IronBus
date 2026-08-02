UPDATE ib_bookings
SET passenger_email = LOWER(TRIM(passenger_email));

CREATE INDEX idx_bookings_passenger_email_created_at
    ON ib_bookings (
                 passenger_email,
                 created_at DESC
        );