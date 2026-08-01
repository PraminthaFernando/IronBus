package com.lsf.ironbus.fare.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FareTest {

    @Test
    void roundsAmountToTwoDecimalPlaces() {
        Fare fare = new Fare(new BigDecimal("125.456"), Currency.getInstance("LKR"));

        assertThat(fare.amount()).isEqualByComparingTo("125.46");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() ->
                new Fare(new BigDecimal("-0.01"), Currency.getInstance("LKR"))
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Fare amount cannot be negative");
    }
}
