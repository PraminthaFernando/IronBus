package com.lsf.ironbus.booking.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SearchBookingsRequest(

        @NotBlank(message = "Passenger email is required")
        @Email(message = "Passenger email must be valid")
        @Size(
                max = 254,
                message = "Passenger email cannot exceed 254 characters"
        )
        String passengerEmail
) {
}