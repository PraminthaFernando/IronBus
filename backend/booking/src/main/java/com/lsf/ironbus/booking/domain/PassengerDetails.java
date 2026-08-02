package com.lsf.ironbus.booking.domain;

public record PassengerDetails(
        String name,
        String email,
        String phone
) {

    public PassengerDetails {
        name = requireText(name, "Passenger name");
        email = requireText(email, "Passenger email");
        phone = requireText(phone, "Passenger phone");
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