package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.exception.BookingNotFoundException;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.booking.repository.BookingSegmentRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelBookingService {

    private final BookingRepository bookingRepository;
    private final BookingSegmentRepository segmentRepository;
    private final BookingMapper bookingMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BookingResponse cancel(String reference) {
        Booking booking = bookingRepository
                .findDetailedByReference(
                        reference.trim().toUpperCase()
                )
                .orElseThrow(() ->
                        new BookingNotFoundException(reference)
                );

        booking.cancel(timeProvider.now());

        int releasedSegments =
                segmentRepository.deleteAllByBookingId(
                        booking.getId()
                );

        if (releasedSegments <= 0) {
            throw new IllegalStateException(
                    "Confirmed booking has no active segment occupancy"
            );
        }

        bookingRepository.flush();

        return bookingMapper.toResponse(booking);
    }
}