package com.lsf.ironbus.booking.domain;

import java.util.Locale;

public record PassengerDetails(
        String name,
        String email,
        String phone
) {

    public PassengerDetails {
        name = requireText(name, "Passenger name");
        email = normalizeEmail(email);
        phone = requireText(phone, "Passenger phone");
    }

    private static String normalizeEmail(String value) {
        String normalized = requireText(
                value,
                "Passenger email"
        ).toLowerCase(Locale.ROOT);

        if (normalized.length() > 254) {
            throw new IllegalArgumentException(
                    "Passenger email cannot exceed 254 characters"
            );
        }

        return normalized;
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        return value.trim();
    }
}