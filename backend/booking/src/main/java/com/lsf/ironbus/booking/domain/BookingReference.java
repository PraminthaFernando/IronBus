package com.lsf.ironbus.booking.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record BookingReference(String value) {

    private static final Pattern PATTERN =
            Pattern.compile("LSF-[0-9]{2}-[A-Z0-9]{6}");

    public BookingReference {
        Objects.requireNonNull(value, "Booking reference is required");

        value = value.trim().toUpperCase(Locale.ROOT);

        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid booking reference format"
            );
        }
    }
}