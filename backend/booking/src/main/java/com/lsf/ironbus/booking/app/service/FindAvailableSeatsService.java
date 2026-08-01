package com.lsf.ironbus.booking.app.service;

import com.lsf.ironbus.booking.app.command.FindAvailableSeatsCommand;
import com.lsf.ironbus.booking.app.response.AvailabilityResponse;
import com.lsf.ironbus.booking.app.response.AvailableSeatResponse;
import com.lsf.ironbus.booking.infra.AvailableSeatProjection;
import com.lsf.ironbus.booking.repository.SeatAvailabilityRepository;
import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.segment.domain.SegmentSequence;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAvailableSeatsService {

    private final JourneyLegResolver journeyLegResolver;
    private final SeatAvailabilityRepository availabilityRepository;
    private final FarePolicy farePolicy;

    @Transactional(readOnly = true)
    public AvailabilityResponse find(
            FindAvailableSeatsCommand command
    ) {
        JourneyLeg leg = journeyLegResolver.resolve(
                new ResolveJourneyLegCommand(
                        command.journeyId(),
                        command.originStationId(),
                        command.destinationStationId()
                )
        );

        List<AvailableSeatResponse> seats =
                availabilityRepository.findAvailableSeats(
                                command.journeyId(),
                                leg.originSequence(),
                                leg.destinationSequence()
                        )
                        .stream()
                        .map(projection -> mapSeat(leg, projection))
                        .toList();

        return new AvailabilityResponse(
                leg.journeyId(),
                leg.originStationId(),
                leg.destinationStationId(),
                leg.originSequence(),
                leg.destinationSequence(),
                leg.distanceKm(),
                leg.segmentRange()
                        .segments()
                        .stream()
                        .map(SegmentSequence::value)
                        .toList(),
                seats
        );
    }

    private AvailableSeatResponse mapSeat(
            JourneyLeg leg,
            AvailableSeatProjection projection
    ) {
        TravelClass travelClass = projection.getTravelClass();
        SeatType seatType = projection.getSeatType();

        Fare fare = farePolicy.calculate(leg, travelClass);

        return new AvailableSeatResponse(
                projection.getSeatId(),
                projection.getCoachId(),
                projection.getCoachNumber(),
                travelClass,
                projection.getSeatNumber(),
                seatType,
                projection.getRowNumber(),
                projection.getColumnNumber(),
                fare.amount(),
                fare.currency().getCurrencyCode()
        );
    }
}