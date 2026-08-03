package com.lsf.ironbus.booking.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerRequest(

        @NotBlank(message = "Passenger name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Passenger name must contain between 2 and 150 characters"
        )
        String name,

        @NotBlank(message = "Passenger email is required")
        @Email(message = "Passenger email must be valid")
        @Size(
                max = 254,
                message = "Passenger email must not exceed 254 characters"
        )
        String email,

        @NotBlank(message = "Passenger phone number is required")
        @Pattern(
                regexp = "^\\+?[0-9][0-9\\s-]{7,19}$",
                message = "Passenger phone number must be valid"
        )
        String phone
) {
}