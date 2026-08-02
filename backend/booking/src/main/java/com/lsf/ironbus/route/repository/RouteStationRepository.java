package com.lsf.ironbus.route.repository;

import com.lsf.ironbus.route.domain.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteStationRepository
        extends JpaRepository<RouteStation, UUID> {

    boolean existsByRouteIdAndStationId(
            UUID routeId,
            UUID stationId
    );

    boolean existsByRouteIdAndSequenceNumber(
            UUID routeId,
            int sequenceNumber
    );

    List<RouteStation>
    findAllByRouteIdAndActiveTrueOrderBySequenceNumberAsc(UUID routeId);

    Optional<RouteStation>
    findFirstByRouteIdAndActiveTrueOrderBySequenceNumberDesc(UUID routeId);

    long countByRouteIdAndActiveTrue(UUID routeId);

    Optional<RouteStation>
    findByRouteIdAndStationIdAndActiveTrue(
            UUID routeId,
            UUID stationId
    );

    Optional<RouteStation>
    findByStationIdAndActiveTrue(UUID stationId);
}