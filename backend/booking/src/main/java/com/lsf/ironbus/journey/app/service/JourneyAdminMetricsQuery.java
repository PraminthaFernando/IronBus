package com.lsf.ironbus.journey.app.service;

import com.lsf.ironbus.journey.app.response.JourneyAdminMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JourneyAdminMetricsQuery {

    private final JdbcTemplate jdbcTemplate;

    public JourneyAdminMetrics findByJourneyId(
            UUID journeyId
    ) {
        return jdbcTemplate.query(
                """
                select
                    (
                        select count(*)
                        from ib_bookings b
                        where b.journey_id = ?
                          and b.status = 'CONFIRMED'
                    ) as booking_count,

                    (
                        select count(*)
                        from ib_booking_segments bs
                        join ib_bookings b
                            on b.id = bs.booking_id
                        where bs.journey_id = ?
                          and b.status = 'CONFIRMED'
                    ) as occupied_segment_count,

                    (
                        select
                            count(distinct s.id)
                            *
                            greatest(
                                count(distinct rs.id) - 1,
                                0
                            )
                        from ib_journeys j
                        join ib_trains t
                            on t.id = j.train_id
                        join ib_coaches c
                            on c.train_id = t.id
                           and c.active = true
                           and c.reservation_mode = 'RESERVED'
                        join ib_seats s
                            on s.coach_id = c.id
                           and s.active = true
                        join ib_route_stations rs
                            on rs.route_id = j.route_id
                           and rs.active = true
                        where j.id = ?
                    ) as total_segment_capacity
                """,
                statement -> {
                    statement.setObject(1, journeyId);
                    statement.setObject(2, journeyId);
                    statement.setObject(3, journeyId);
                },
                resultSet -> {
                    if (!resultSet.next()) {
                        return JourneyAdminMetrics.empty();
                    }

                    return new JourneyAdminMetrics(
                            resultSet.getLong(
                                    "booking_count"
                            ),
                            resultSet.getLong(
                                    "occupied_segment_count"
                            ),
                            resultSet.getLong(
                                    "total_segment_capacity"
                            )
                    );
                }
        );
    }
}