package com.lsf.ironbus.fare.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Fare(
        BigDecimal amount,
        Currency currency
) {

    public Fare {
        Objects.requireNonNull(amount, "Fare amount is required");
        Objects.requireNonNull(currency, "Currency is required");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Fare amount cannot be negative"
            );
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
}