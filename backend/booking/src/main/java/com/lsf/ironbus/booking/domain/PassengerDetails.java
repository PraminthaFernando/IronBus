package com.lsf.ironbus.booking.domain;

import java.util.Locale;
import java.util.Objects;

public record PassengerDetails(
        String name,
        String email,
        String phone
) {

    public PassengerDetails {
        name = normalizeRequired(name, "name");
        email = normalizeRequired(email, "email")
                .toLowerCase(Locale.ROOT);
        phone = normalizeRequired(phone, "phone")
                .replace(" ", "")
                .replace("-", "");
    }

    private static String normalizeRequired(
            String value,
            String fieldName
    ) {
        Objects.requireNonNull(
                value,
                fieldName + " must not be null"
        );

        String normalized = value
                .trim()
                .replaceAll("\\s+", " ");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return normalized;
    }
}