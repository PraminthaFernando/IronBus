package com.lsf.ironbus.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ironbus.openapi")
public record OpenApiProperties(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String version,
        @NotBlank String serverUrl
) {
}