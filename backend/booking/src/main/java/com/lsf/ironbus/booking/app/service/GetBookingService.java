package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.exception.BookingNotFoundException;
import com.lsf.ironbus.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetBookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public BookingResponse get(String reference) {
        Booking booking = bookingRepository
                .findDetailedByReference(
                        reference.trim().toUpperCase()
                )
                .orElseThrow(() ->
                        new BookingNotFoundException(reference)
                );

        return bookingMapper.toResponse(booking);
    }
}