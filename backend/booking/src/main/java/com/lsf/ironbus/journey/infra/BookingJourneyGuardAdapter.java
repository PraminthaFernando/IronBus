package com.lsf.ironbus.journey.infra;

import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.journey.app.service.JourneyBookingGuard;
import com.lsf.ironbus.journey.exception.JourneyHasBookingsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingJourneyGuardAdapter
        implements JourneyBookingGuard {

    private final BookingRepository bookingRepository;

    @Override
    public boolean hasBookings(UUID journeyId) {
        return bookingRepository.existsByJourneyId(journeyId);
    }

    @Override
    public void assertCanReschedule(UUID journeyId) {
        boolean hasConfirmedBookings =
                bookingRepository.existsByJourneyIdAndStatus(
                        journeyId,
                        BookingStatus.CONFIRMED
                );

        if (hasConfirmedBookings) {
            throw new JourneyHasBookingsException(journeyId);
        }
    }

    @Override
    public void assertCanCancel(UUID journeyId) {
        /*
         * Choose one business rule:
         *
         * 1. Allow cancellation and cancel/refund all bookings.
         * 2. Reject cancellation while confirmed bookings exist.
         *
         * For the assessment admin module, rejecting is safer unless
         * automatic booking cancellation is already implemented.
         */
        boolean hasConfirmedBookings =
                bookingRepository.existsByJourneyIdAndStatus(
                        journeyId,
                        BookingStatus.CONFIRMED
                );

        if (hasConfirmedBookings) {
            throw new JourneyHasBookingsException(journeyId);
        }
    }

    @Override
    public void assertCanReactivate(UUID journeyId) {
        // Add additional checks when needed.
    }
}