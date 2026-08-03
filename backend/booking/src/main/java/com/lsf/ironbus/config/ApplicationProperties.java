package com.lsf.ironbus.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "ironbus")
public record ApplicationProperties(
        @NotBlank String frontendUrl,
        @NotNull @Valid BookingProperties booking
) {

    public record BookingProperties(
            @NotBlank String referencePrefix,
            @NotNull Duration cancellationCutoff
    ) {
    }
}