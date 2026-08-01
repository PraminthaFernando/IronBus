package com.lsf.ironbus.fare.domain;

import com.lsf.ironbus.fare.config.FareProperties;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static com.lsf.ironbus.support.Phase2Fixtures.journeyLeg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceBasedFarePolicyTest {

    private DistanceBasedFarePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DistanceBasedFarePolicy(new FareProperties(
                "LKR",
                new BigDecimal("100.00"),
                new BigDecimal("8.00"),
                new BigDecimal("150.00"),
                Map.of(
                        TravelClass.FIRST_CLASS, new BigDecimal("1.75"),
                        TravelClass.SECOND_CLASS, new BigDecimal("1.25"),
                        TravelClass.THIRD_CLASS, new BigDecimal("1.00")
                )
        ));
    }

    @Test
    void calculatesFaresForEveryClass() {
        var leg = journeyLeg(0, 2, "0.00", "120.00");

        assertThat(policy.calculate(leg, TravelClass.FIRST_CLASS).amount())
                .isEqualByComparingTo("1780.00");
        assertThat(policy.calculate(leg, TravelClass.SECOND_CLASS).amount())
                .isEqualByComparingTo("1300.00");
        assertThat(policy.calculate(leg, TravelClass.THIRD_CLASS).amount())
                .isEqualByComparingTo("1060.00");
    }

    @Test
    void appliesMinimumFare() {
        var fare = policy.calculate(
                journeyLeg(0, 1, "0.00", "1.00"),
                TravelClass.THIRD_CLASS
        );

        assertThat(fare.amount()).isEqualByComparingTo("150.00");
    }

    @Test
    void rejectsMissingMultiplier() {
        var incomplete = new DistanceBasedFarePolicy(new FareProperties(
                "LKR",
                new BigDecimal("100.00"),
                new BigDecimal("8.00"),
                new BigDecimal("150.00"),
                Map.of(TravelClass.SECOND_CLASS, new BigDecimal("1.25"))
        ));

        assertThatThrownBy(() ->
                incomplete.calculate(
                        journeyLeg(0, 1, "0.00", "10.00"),
                        TravelClass.FIRST_CLASS
                )
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("No fare multiplier configured");
    }
}
