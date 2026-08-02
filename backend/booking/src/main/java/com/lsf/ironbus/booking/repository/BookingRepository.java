package com.lsf.ironbus.booking.repository;

import com.lsf.ironbus.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository
        extends JpaRepository<Booking, UUID> {

    boolean existsByReference(String reference);

    @Query("""
        select b
        from Booking b
        join fetch b.journey j
        join fetch b.seat s
        join fetch s.coach c
        join fetch b.originRouteStation ors
        join fetch ors.station os
        join fetch b.destinationRouteStation drs
        join fetch drs.station ds
        where b.reference = :reference
        """)
    Optional<Booking> findDetailedByReference(String reference);
}