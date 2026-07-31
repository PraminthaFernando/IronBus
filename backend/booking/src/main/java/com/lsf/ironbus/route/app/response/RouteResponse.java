package com.lsf.ironbus.route.app.response;

import com.lsf.ironbus.route.domain.Route;

import java.time.Instant;
import java.util.UUID;

public record RouteResponse(
        UUID id,
        String code,
        String name,
        boolean active,
        Instant createdAt,
        long version
) {

    public static RouteResponse from(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getCode(),
                route.getName(),
                route.isActive(),
                route.getCreatedAt(),
                route.getVersion()
        );
    }
}