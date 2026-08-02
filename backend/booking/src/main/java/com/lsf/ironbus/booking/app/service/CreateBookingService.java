package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.command.CreateBookingCommand;
import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.domain.*;
import com.lsf.ironbus.booking.exception.SeatNotFoundException;
import com.lsf.ironbus.booking.exception.SeatNotOnJourneyTrainException;
import com.lsf.ironbus.booking.exception.SeatNotReservableException;
import com.lsf.ironbus.booking.exception.SeatSegmentConflictException;
import com.lsf.ironbus.booking.infra.*;
import com.lsf.ironbus.booking.repository.BookingRepository;
import com.lsf.ironbus.booking.repository.BookingSegmentRepository;
import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.exception.JourneyNotAvailableException;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.shared.error.ResourceNotFoundException;
import com.lsf.ironbus.shared.persistence.ConstraintViolationInspector;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateBookingService {

    private final JourneyRepository journeyRepository;
    private final SeatRepository seatRepository;
    private final RouteStationRepository routeStationRepository;
    private final BookingRepository bookingRepository;
    private final BookingSegmentRepository bookingSegmentRepository;
    private final JourneyLegResolver journeyLegResolver;
    private final FarePolicy farePolicy;
    private final BookingReferenceGenerator referenceGenerator;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse create(CreateBookingCommand command) {
        Journey journey = journeyRepository
                .findDetailedById(command.journeyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Journey",
                        command.journeyId()
                ));

        if (!journey.isBookable()) {
            throw new JourneyNotAvailableException(journey.getId());
        }

        JourneyLeg leg = journeyLegResolver.resolve(
                new ResolveJourneyLegCommand(
                        command.journeyId(),
                        command.originStationId(),
                        command.destinationStationId()
                )
        );

        Seat seat = seatRepository
                .findDetailedActiveById(command.seatId())
                .orElseThrow(() -> new SeatNotFoundException(
                        command.seatId()
                ));

        validateSeat(journey, seat);

        RouteStation origin = routeStationRepository
                .findByStationIdAndActiveTrue(
                        leg.originRouteStationId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RouteStation",
                        leg.originRouteStationId()
                ));

        RouteStation destination = routeStationRepository
                .findByStationIdAndActiveTrue(
                        leg.destinationRouteStationId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RouteStation",
                        leg.destinationRouteStationId()
                ));

        Fare fare = farePolicy.calculate(
                leg,
                seat.getCoach().getTravelClass()
        );

        Booking booking = new Booking(
                uuidGenerator.generate(),
                generateUniqueReference(),
                journey,
                seat,
                origin,
                destination,
                leg.originSequence(),
                leg.destinationSequence(),
                new PassengerDetails(
                        command.passengerName(),
                        command.passengerEmail(),
                        command.passengerPhone()
                ),
                fare.amount(),
                fare.currency(),
                timeProvider.now()
        );

        bookingRepository.save(booking);

        List<BookingSegment> segments =
                leg.segmentRange()
                        .segments()
                        .stream()
                        .map(segment -> new BookingSegment(
                                uuidGenerator.generate(),
                                booking,
                                journey,
                                seat,
                                segment.value()
                        ))
                        .toList();

        try {
            bookingSegmentRepository.saveAll(segments);

            // Force INSERT statements before returning.
            bookingSegmentRepository.flush();
            bookingRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (ConstraintViolationInspector.causedByConstraint(
                    exception,
                    "uk_journey_seat_segment"
            )) {
                throw new SeatSegmentConflictException(
                        journey.getId(),
                        seat.getId()
                );
            }

            throw exception;
        }

        return bookingMapper.toResponse(booking);
    }

    private void validateSeat(
            Journey journey,
            Seat seat
    ) {
        if (!seat.getCoach().isReserved()) {
            throw new SeatNotReservableException(seat.getId());
        }

        if (!seat.getCoach()
                .getTrain()
                .getId()
                .equals(journey.getTrain().getId())) {
            throw new SeatNotOnJourneyTrainException(
                    seat.getId(),
                    journey.getId()
            );
        }
    }

    private BookingReference generateUniqueReference() {
        for (int attempt = 0; attempt < 5; attempt++) {
            BookingReference candidate =
                    referenceGenerator.generate();

            if (!bookingRepository.existsByReference(
                    candidate.value()
            )) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique booking reference"
        );
    }
}