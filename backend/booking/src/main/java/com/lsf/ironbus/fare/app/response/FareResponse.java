package com.lsf.ironbus.fare.app.response;

import java.math.BigDecimal;

public record FareResponse(
        BigDecimal amount,
        String currency
) {
}