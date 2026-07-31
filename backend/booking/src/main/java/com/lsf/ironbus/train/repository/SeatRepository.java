package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SeatRepository
        extends JpaRepository<Seat, UUID> {

    boolean existsByCoachIdAndSeatNumberIgnoreCase(
            UUID coachId,
            String seatNumber
    );

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
}