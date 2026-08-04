package com.lsf.ironbus.journey.app.response;

import com.lsf.ironbus.journey.app.response.JourneyAdminResponse;
import com.lsf.ironbus.journey.app.service.JourneyAdminMetricsQuery;
import com.lsf.ironbus.journey.domain.Journey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class JourneyAdminMapper {

    private final JourneyAdminMetricsQuery metricsQuery;

    public JourneyAdminResponse toAdminResponse(Journey journey) {
        JourneyAdminMetrics metrics =
                metricsQuery.findByJourneyId(journey.getId());

        return new JourneyAdminResponse(
                journey.getId(),
                journey.getTrain().getId(),
                journey.getTrain().getCode(),
                journey.getRoute().getId(),
                journey.getRoute().getCode(),
                journey.getDepartureTime(),
                journey.getStatus(),
                metrics.bookingCount(),
                calculateOccupancyPercentage(
                        metrics.occupiedSegmentCount(),
                        metrics.totalSegmentCapacity()
                ),
                journey.getVersion()
        );
    }

    private static BigDecimal calculateOccupancyPercentage(
            long occupiedSegmentCount,
            long totalSegmentCapacity
    ) {
        if (totalSegmentCapacity <= 0) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return BigDecimal
                .valueOf(occupiedSegmentCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(totalSegmentCapacity),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}