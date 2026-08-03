package com.lsf.ironbus.route.app.service;

import com.lsf.ironbus.route.exception.InvalidRouteDefinitionException;
import com.lsf.ironbus.route.web.request.ReplaceRouteStationsRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class RouteStationDefinitionValidator {

    public void validate(
            List<ReplaceRouteStationsRequest.RouteStationItem> items
    ) {
        if (items.size() < 2) {
            throw new InvalidRouteDefinitionException(
                    "A route requires at least two stations"
            );
        }

        Set<UUID> stationIds = new HashSet<>();

        BigDecimal previousDistance = null;
        int previousOffset = -1;

        for (int index = 0; index < items.size(); index++) {
            var item = items.get(index);

            if (item.sequenceNumber() != index) {
                throw new InvalidRouteDefinitionException(
                        "Route station sequences must be contiguous from zero"
                );
            }

            if (!stationIds.add(item.stationId())) {
                throw new InvalidRouteDefinitionException(
                        "A station cannot appear more than once on a route"
                );
            }

            if (index == 0
                    && item.distanceFromOriginKm()
                    .compareTo(BigDecimal.ZERO) != 0) {
                throw new InvalidRouteDefinitionException(
                        "The first station distance must be zero"
                );
            }

            if (previousDistance != null
                    && item.distanceFromOriginKm()
                    .compareTo(previousDistance) <= 0) {
                throw new InvalidRouteDefinitionException(
                        "Route distances must strictly increase"
                );
            }

            if (item.scheduledOffsetMinutes() < previousOffset) {
                throw new InvalidRouteDefinitionException(
                        "Scheduled offsets must not decrease"
                );
            }

            previousDistance = item.distanceFromOriginKm();
            previousOffset = item.scheduledOffsetMinutes();
        }
    }
}