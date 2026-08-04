package com.lsf.ironbus.train.app.projection;

import java.util.UUID;

public interface TrainAdminProjection {

    UUID getId();

    String getCode();

    String getName();

    boolean getActive();

    long getCoachCount();

    long getReservedCoachCount();

    long getSeatCount();

    long getVersion();
}