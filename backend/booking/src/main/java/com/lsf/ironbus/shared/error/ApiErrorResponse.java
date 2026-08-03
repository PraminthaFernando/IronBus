package com.lsf.ironbus.shared.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, String> fieldErrors
) {

    public static ApiErrorResponse of(
            int status,
            String code,
            String message,
            String path,
            String traceId
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                code,
                message,
                path,
                traceId,
                Map.of()
        );
    }

    public static ApiErrorResponse withFieldErrors(
            int status,
            String code,
            String message,
            String path,
            String traceId,
            Map<String, String> fieldErrors
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                code,
                message,
                path,
                traceId,
                Map.copyOf(fieldErrors)
        );
    }
}