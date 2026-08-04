package com.lsf.ironbus.train.app.projection;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public interface CoachAdminProjection {

    UUID getId();

    UUID getTrainId();

    String getCoachNumber();

    TravelClass getTravelClass();

    CoachReservationMode getReservationMode();

    boolean getActive();

    long getSeatCount();

    long getVersion();
}