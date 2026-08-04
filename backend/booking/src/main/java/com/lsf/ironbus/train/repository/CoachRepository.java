package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.app.projection.CoachAdminProjection;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachRepository
        extends JpaRepository<Coach, UUID> {

    boolean existsByTrainIdAndCoachNumberIgnoreCase(
            UUID trainId,
            String coachNumber
    );

    Optional<Coach> findByIdAndActiveTrue(UUID id);

    List<Coach> findAllByTrainIdAndActiveTrueOrderByCoachNumberAsc(
            UUID trainId
    );

    long countByActiveTrue();

    Page<Coach> findByTrainId(UUID trainId, Pageable pageable);

    Optional<Coach>
    findByTrainIdAndCoachNumberIgnoreCase(
            UUID trainId,
            String coachNumber
    );

    long countByReservationMode(
            CoachReservationMode reservationMode
    );

    @Query("""
            select distinct c
            from Coach c
            left join fetch c.seats
            where c.id = :coachId
            """)
    Optional<Coach> findByIdWithSeats(
            @Param("coachId") UUID coachId
    );

    @Query(
            value = """
                select
                    c.id as id,
                    c.train_id as trainId,
                    c.coach_number as coachNumber,
                    c.travel_class as travelClass,
                    c.reservation_mode as reservationMode,
                    c.active as active,
                    count(s.id) as seatCount,
                    c.version as version
                from ib_coaches c
                left join ib_seats s
                    on s.coach_id = c.id
                where c.train_id = :trainId
                group by
                    c.id,
                    c.train_id,
                    c.coach_number,
                    c.travel_class,
                    c.reservation_mode,
                    c.active,
                    c.version
                """,
            countQuery = """
                select count(*)
                from ib_coaches c
                where c.train_id = :trainId
                """,
            nativeQuery = true
    )
    Page<CoachAdminProjection> findAdminPageByTrainId(
            @Param("trainId") UUID trainId,
            Pageable pageable
    );
}