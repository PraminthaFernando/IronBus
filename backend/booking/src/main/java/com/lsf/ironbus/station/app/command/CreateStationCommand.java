package com.lsf.ironbus.station.app.command;

public record CreateStationCommand(
        String code,
        String name
) {
}