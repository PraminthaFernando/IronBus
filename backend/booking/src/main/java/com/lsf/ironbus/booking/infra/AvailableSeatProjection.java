package com.lsf.ironbus.booking.infra;

import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;

import java.util.UUID;

public interface AvailableSeatProjection {

    UUID getSeatId();

    UUID getCoachId();

    String getCoachNumber();

    TravelClass getTravelClass();

    String getSeatNumber();

    SeatType getSeatType();

    Integer getRowNumber();

    Integer getColumnNumber();
}