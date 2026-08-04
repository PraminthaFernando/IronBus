package com.lsf.ironbus.admin.repository;

import com.lsf.ironbus.admin.web.response.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminWarningQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<AdminDashboardResponse.ConfigurationWarning>
    findWarnings() {
        List<AdminDashboardResponse.ConfigurationWarning> result =
                new ArrayList<>();

        result.addAll(findRoutesWithTooFewStations());
        result.addAll(findTrainsWithoutCoaches());
        result.addAll(findReservedCoachesWithoutSeats());

        return List.copyOf(result);
    }

    private List<AdminDashboardResponse.ConfigurationWarning>
    findRoutesWithTooFewStations() {
        return jdbcTemplate.query(
                """
                select
                    r.id,
                    r.code
                from ib_routes r
                left join ib_route_stations rs
                    on rs.route_id = r.id
                   and rs.active = true
                where r.active = true
                group by r.id, r.code
                having count(rs.id) < 2
                order by r.code
                """,
                (rs, rowNum) ->
                        new AdminDashboardResponse.ConfigurationWarning(
                                "ROUTE_TOO_SHORT",
                                "ROUTE",
                                rs.getObject(
                                        "id",
                                        UUID.class
                                ),
                                "Route "
                                        + rs.getString("code")
                                        + " has fewer than two active stations"
                        )
        );
    }

    private List<AdminDashboardResponse.ConfigurationWarning>
    findTrainsWithoutCoaches() {
        return jdbcTemplate.query(
                """
                select
                    t.id,
                    t.code
                from ib_trains t
                left join ib_coaches c
                    on c.train_id = t.id
                   and c.active = true
                where t.active = true
                group by t.id, t.code
                having count(c.id) = 0
                order by t.code
                """,
                (rs, rowNum) ->
                        new AdminDashboardResponse.ConfigurationWarning(
                                "TRAIN_NO_COACHES",
                                "TRAIN",
                                rs.getObject(
                                        "id",
                                        UUID.class
                                ),
                                "Train "
                                        + rs.getString("code")
                                        + " has no active coaches"
                        )
        );
    }

    private List<AdminDashboardResponse.ConfigurationWarning>
    findReservedCoachesWithoutSeats() {
        return jdbcTemplate.query(
                """
                select
                    c.id,
                    c.coach_number,
                    t.code as train_code
                from ib_coaches c
                join ib_trains t
                    on t.id = c.train_id
                left join ib_seats s
                    on s.coach_id = c.id
                   and s.active = true
                where c.active = true
                  and t.active = true
                  and c.reservation_mode = 'RESERVED'
                group by
                    c.id,
                    c.coach_number,
                    t.code
                having count(s.id) = 0
                order by
                    t.code,
                    c.coach_number
                """,
                (rs, rowNum) ->
                        new AdminDashboardResponse.ConfigurationWarning(
                                "COACH_NO_SEATS",
                                "COACH",
                                rs.getObject(
                                        "id",
                                        UUID.class
                                ),
                                "Reserved coach "
                                        + rs.getString("coach_number")
                                        + " on train "
                                        + rs.getString("train_code")
                                        + " has no active seats"
                        )
        );
    }
}