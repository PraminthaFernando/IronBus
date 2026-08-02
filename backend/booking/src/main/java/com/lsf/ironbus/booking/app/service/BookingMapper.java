package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.domain.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getReference(),
                booking.getStatus(),
                booking.getJourney().getId(),
                booking.getJourney().getDepartureTime(),
                booking.getSeat().getId(),
                booking.getSeat().getCoach().getCoachNumber(),
                booking.getSeat().getSeatNumber(),
                booking.getSeat().getCoach().getTravelClass(),
                booking.getOriginRouteStation()
                        .getStation()
                        .getCode(),
                booking.getOriginRouteStation()
                        .getStation()
                        .getName(),
                booking.getDestinationRouteStation()
                        .getStation()
                        .getCode(),
                booking.getDestinationRouteStation()
                        .getStation()
                        .getName(),
                booking.getFareAmount(),
                booking.getCurrency(),
                booking.getCreatedAt()
        );
    }
}