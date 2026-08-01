package com.lsf.ironbus.fare.domain;

import com.lsf.ironbus.segment.domain.JourneyLeg;
import com.lsf.ironbus.train.enums.TravelClass;

public interface FarePolicy {

    Fare calculate(
            JourneyLeg journeyLeg,
            TravelClass travelClass
    );
}