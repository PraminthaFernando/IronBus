package com.lsf.ironbus.shared.error;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, String> validationErrors
) {
}