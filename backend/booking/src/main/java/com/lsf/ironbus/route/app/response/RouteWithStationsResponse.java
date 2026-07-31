package com.lsf.ironbus.route.app.response;

import java.util.List;
import java.util.UUID;

public record RouteWithStationsResponse(
        UUID routeId,
        String code,
        String name,
        List<RouteStationResponse> stations
) {

    public RouteWithStationsResponse {
        stations = List.copyOf(stations);
    }
}