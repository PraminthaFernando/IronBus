package com.lsf.ironbus.journey.repository;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
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
}