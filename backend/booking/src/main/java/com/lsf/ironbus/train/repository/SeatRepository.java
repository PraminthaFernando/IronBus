package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository
        extends JpaRepository<Seat, UUID> {

    boolean existsByCoachIdAndSeatNumberIgnoreCase(
            UUID coachId,
            String seatNumber
    );

    boolean existsByCoachId(UUID coachId);

    List<Seat> findAllByCoachIdAndActiveTrueOrderBySeatNumberAsc(
            UUID coachId
    );

    List<Seat> findAllByCoachTrainIdAndActiveTrueOrderByCoachCoachNumberAscSeatNumberAsc(
            UUID trainId
    );

    @Query("""
    select s
    from Seat s
    join fetch s.coach c
    where c.train.id = :trainId
      and c.active = true
      and s.active = true
    order by c.coachNumber, s.seatNumber
    """)
    List<Seat> findActiveSeatsByTrainId(UUID trainId);

    @Query("""
    select s
    from Seat s
    join fetch s.coach c
    join fetch c.train t
    where s.id = :seatId
      and s.active = true
      and c.active = true
    """)
    Optional<Seat> findDetailedActiveById(UUID seatId);

    long countByActiveTrue();

    Page<Seat> findByCoachId(
            UUID coachId,
            Pageable pageable
    );

    Page<Seat>
    findByCoachIdAndSeatNumberContainingIgnoreCase(
            UUID coachId,
            String seatNumber,
            Pageable pageable
    );

    Optional<Seat>
    findByCoachIdAndSeatNumberIgnoreCase(
            UUID coachId,
            String seatNumber
    );

    @Query("""
            select s.seatNumber
            from Seat s
            where s.coach.id = :coachId
            """)
    List<String> findSeatNumbersByCoachId(
            @Param("coachId") UUID coachId
    );
}