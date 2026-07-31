package com.lsf.ironbus.route.app.command;

public record CreateRouteCommand(
        String code,
        String name
) {
}