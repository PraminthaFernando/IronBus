package com.lsf.ironbus.journey.repository;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JourneyRepository
        extends JpaRepository<Journey, UUID> {

    boolean existsByTrainIdAndDepartureTime(
            UUID trainId,
            Instant departureTime
    );

    Optional<Journey> findByIdAndStatusNot(
            UUID id,
            JourneyStatus excludedStatus
    );

    List<Journey> findAllByRouteIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndStatus(
            UUID routeId,
            Instant start,
            Instant end,
            JourneyStatus status
    );

    @Query("""
    select j
    from Journey j
    join fetch j.route r
    join fetch j.train t
    where j.id = :journeyId
    """)
    Optional<Journey> findDetailedById(UUID journeyId);

    long countByStatusNot(JourneyStatus excludedStatus);

    long countByDepartureTimeAfterAndStatus(Instant departureTime, JourneyStatus status);

    @Query("""
    SELECT COUNT(*)
    FROM Journey j
    WHERE j.departureTime >= :startOfToday
        AND j.departureTime <= :startOfTomorrow
    """)
    long countByDepartureTimeGreaterThanEqualAndDepartureTimeLessThan(
        @Param("startOfToday") Instant startOfToday,
        @Param("startOfTomorrow") Instant startOfTomorrow
    );

    long countByStatus(JourneyStatus status);

    List<Journey>
    findTop10ByStatusAndDepartureTimeAfterOrderByDepartureTimeAsc(
        JourneyStatus status,
        Instant departureTime);

    boolean existsByTrainIdAndStatusIn(
            UUID trainId,
            Collection<JourneyStatus> statuses
    );

    List<Journey>
    findAllByTrainIdAndDepartureTimeAfterAndStatus(
            UUID trainId,
            Instant departureTime,
            JourneyStatus status
    );
}