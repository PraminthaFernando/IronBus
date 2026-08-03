package com.lsf.ironbus.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "ironbus.cors")
public record CorsProperties(

        @NotEmpty(message = "At least one allowed origin is required")
        List<String> allowedOrigins,

        @NotEmpty(message = "At least one allowed method is required")
        List<String> allowedMethods,

        @NotEmpty(message = "At least one allowed header is required")
        List<String> allowedHeaders,

        List<String> exposedHeaders,

        boolean allowCredentials,

        @PositiveOrZero
        long maxAgeSeconds
) {

    public CorsProperties {
        allowedOrigins = immutableOrEmpty(allowedOrigins);
        allowedMethods = immutableOrEmpty(allowedMethods);
        allowedHeaders = immutableOrEmpty(allowedHeaders);
        exposedHeaders = immutableOrEmpty(exposedHeaders);
    }

    private static List<String> immutableOrEmpty(
            List<String> values
    ) {
        return values == null
                ? List.of()
                : List.copyOf(values);
    }
}