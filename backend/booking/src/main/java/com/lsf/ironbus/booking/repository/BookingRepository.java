package com.lsf.ironbus.booking.repository;

import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query(
            value = """
                select b
                from Booking b
                join fetch b.journey j
                join fetch b.seat s
                join fetch s.coach c
                join fetch b.originRouteStation originRouteStation
                join fetch originRouteStation.station originStation
                join fetch b.destinationRouteStation destinationRouteStation
                join fetch destinationRouteStation.station destinationStation
                where b.passengerEmail = :passengerEmail
                order by b.createdAt desc
                """,
            countQuery = """
                select count(b)
                from Booking b
                where b.passengerEmail = :passengerEmail
                """
    )
    Page<Booking> findDetailedByPassengerEmail(
            @Param("passengerEmail")
            String passengerEmail,
            Pageable pageable
    );

    boolean existsByJourneyId(UUID journeyId);

    boolean existsByJourneyIdAndStatus(
            UUID journeyId,
            BookingStatus status
    );
}