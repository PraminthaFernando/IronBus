package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.response.BookingSearchItemResponse;
import com.lsf.ironbus.booking.app.response.BookingSearchResponse;
import com.lsf.ironbus.booking.domain.Booking;
import com.lsf.ironbus.booking.exception.BookingNotFoundException;
import com.lsf.ironbus.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GetBookingService {

    private static final int MAX_PAGE_SIZE = 50;

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

    @Transactional(readOnly = true)
    public BookingSearchResponse searchByPassengerEmail(
            String passengerEmail,
            int page,
            int size
    ) {
        String normalizedEmail =
                normalizeEmail(passengerEmail);

        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1,
                MAX_PAGE_SIZE
        );

        PageRequest pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<BookingSearchItemResponse> result =
                bookingRepository
                        .findDetailedByPassengerEmail(
                                normalizedEmail,
                                pageable
                        )
                        .map(bookingMapper::toSearchItem);

        return new BookingSearchResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Passenger email is required"
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}