package com.lsf.ironbus.train.app.command;

public record CreateTrainCommand(
        String code,
        String name
) {
}