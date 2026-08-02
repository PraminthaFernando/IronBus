package com.lsf.ironbus.booking.repository;

import com.lsf.ironbus.booking.domain.BookingSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BookingSegmentRepository
        extends JpaRepository<BookingSegment, UUID> {

    @Modifying
    @Query("""
        delete from BookingSegment bs
        where bs.booking.id = :bookingId
        """)
    int deleteAllByBookingId(UUID bookingId);

    long countByBookingId(UUID bookingId);
}