package com.lsf.ironbus.fare.config;

import com.lsf.ironbus.train.enums.TravelClass;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "app.fare")
public record FareProperties(
        String currency,
        BigDecimal baseFare,
        BigDecimal pricePerKm,
        BigDecimal minimumFare,
        Map<TravelClass, BigDecimal> classMultipliers
) {
}