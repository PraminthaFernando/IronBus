package com.lsf.ironbus.route.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateRouteRequest(
        @NotBlank
        @Size(min = 2, max = 30)
        String code,

        @NotBlank
        @Size(min = 3, max = 150)
        String name,

        boolean active,

        @NotNull
        @PositiveOrZero
        Long expectedVersion
) {
}