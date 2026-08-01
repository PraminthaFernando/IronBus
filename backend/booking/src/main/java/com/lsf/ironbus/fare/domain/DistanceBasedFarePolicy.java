package com.lsf.ironbus.fare.domain;

import com.lsf.ironbus.fare.config.FareProperties;
import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.train.enums.TravelClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Component
@RequiredArgsConstructor
public class DistanceBasedFarePolicy implements FarePolicy {

    private final FareProperties properties;

    @Override
    public Fare calculate(
            JourneyLeg journeyLeg,
            TravelClass travelClass
    ) {
        BigDecimal multiplier =
                properties.classMultipliers().get(travelClass);

        if (multiplier == null) {
            throw new IllegalStateException(
                    "No fare multiplier configured for "
                            + travelClass
            );
        }

        BigDecimal distanceCharge =
                journeyLeg.distanceKm()
                        .multiply(properties.pricePerKm())
                        .multiply(multiplier);

        BigDecimal calculated =
                properties.baseFare()
                        .add(distanceCharge)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalAmount =
                calculated.max(properties.minimumFare());

        return new Fare(
                finalAmount,
                Currency.getInstance(properties.currency())
        );
    }
}