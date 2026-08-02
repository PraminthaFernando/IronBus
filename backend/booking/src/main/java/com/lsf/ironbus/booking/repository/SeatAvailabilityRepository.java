package com.lsf.ironbus.booking.repository;

import com.lsf.ironbus.booking.infra.AvailableSeatProjection;
import com.lsf.ironbus.train.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SeatAvailabilityRepository
        extends JpaRepository<Seat, UUID> {

    @Query(
            value = """
                SELECT
                    s.id AS seatId,
                    c.id AS coachId,
                    c.coach_number AS coachNumber,
                    c.travel_class AS travelClass,
                    s.seat_number AS seatNumber,
                    s.seat_type AS seatType,
                    s.row_number AS rowNumber,
                    s.column_number AS columnNumber
                FROM ib_seats s
                JOIN ib_coaches c
                  ON c.id = s.coach_id
                JOIN ib_journeys j
                  ON j.train_id = c.train_id
                WHERE j.id = :journeyId
                  AND j.status = 'SCHEDULED'
                  AND c.active = TRUE
                  AND s.active = TRUE
                  AND c.reservation_mode = 'RESERVED'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM ib_booking_segments bs
                      WHERE bs.journey_id = :journeyId
                        AND bs.seat_id = s.id
                        AND bs.segment_sequence >= :originSequence
                        AND bs.segment_sequence < :destinationSequence
                  )
                ORDER BY
                    c.coach_number,
                    s.row_number NULLS LAST,
                    s.column_number NULLS LAST,
                    s.seat_number
                """,
            nativeQuery = true
    )
    List<AvailableSeatProjection> findAvailableSeats(
            @Param("journeyId") UUID journeyId,
            @Param("originSequence") int originSequence,
            @Param("destinationSequence") int destinationSequence
    );
}