package com.lsf.ironbus.train.web.request;

import com.lsf.ironbus.train.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddSeatRequest(
        @NotBlank
        @Size(max = 20)
        String seatNumber,

        @NotNull
        SeatType seatType,

        @Positive
        Integer rowNumber,

        @Positive
        Integer columnNumber,

        boolean active
) {
}