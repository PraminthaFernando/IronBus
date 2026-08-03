package com.lsf.ironbus.admin.web.response;


import com.lsf.ironbus.journey.app.response.JourneyAdminResponse;

import java.util.List;
import java.util.UUID;

public record AdminDashboardResponse(
        Metric stations,
        Metric routes,
        Metric trains,
        CoachMetric coaches,
        Metric seats,
        JourneyMetric journeys,
        List<ConfigurationWarning> warnings,
        List<JourneyAdminResponse> upcomingJourneys
) {

    public record Metric(
            long total,
            long active
    ) {
    }

    public record CoachMetric(
            long total,
            long active,
            long reserved,
            long unreserved
    ) {
    }

    public record JourneyMetric(
            long total,
            long active,
            long upcoming,
            long today,
            long cancelled
    ) {
    }

    public record ConfigurationWarning(
            String code,
            String resourceType,
            UUID resourceId,
            String message
    ) {
    }
}