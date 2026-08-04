package com.lsf.ironbus.journey.app.service;

import java.util.UUID;

public interface JourneyBookingGuard {

    boolean hasBookings(UUID journeyId);

    void assertCanReschedule(UUID journeyId);

    void assertCanCancel(UUID journeyId);

    void assertCanReactivate(UUID journeyId);
}