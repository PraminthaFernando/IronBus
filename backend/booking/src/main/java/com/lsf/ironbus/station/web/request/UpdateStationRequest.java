package com.lsf.ironbus.station.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateStationRequest(

        @NotBlank
        @Size(min = 2, max = 10)
        @Pattern(regexp = "^[A-Z0-9-]+$")
        String code,

        @NotBlank
        @Size(min = 2, max = 150)
        String name,

        boolean active,

        @NotNull
        @PositiveOrZero
        Long expectedVersion
) {
}