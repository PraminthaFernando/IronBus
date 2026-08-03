package com.lsf.ironbus.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_ATTRIBUTE = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String MDC_TRACE_ID = "traceId";

    private static final Pattern SAFE_TRACE_ID =
            Pattern.compile("^[a-zA-Z0-9\\-]{8,64}$");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String traceId = resolveTraceId(request);

        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        try (MDC.MDCCloseable ignored = MDC.putCloseable(MDC_TRACE_ID, traceId)) {
            filterChain.doFilter(request, response);
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String suppliedTraceId = request.getHeader(TRACE_ID_HEADER);

        if (StringUtils.hasText(suppliedTraceId)
                && SAFE_TRACE_ID.matcher(suppliedTraceId).matches()) {
            return suppliedTraceId;
        }

        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}