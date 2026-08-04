package com.lsf.ironbus.journey.app.response;

public record JourneyAdminMetrics(
        long bookingCount,
        long occupiedSegmentCount,
        long totalSegmentCapacity
) {

    public static JourneyAdminMetrics empty() {
        return new JourneyAdminMetrics(
                0,
                0,
                0
        );
    }
}