package com.lsf.ironbus.train.web.request;

import com.lsf.ironbus.train.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSeatRequest(

        @NotBlank
        @Size(max = 20)
        String seatNumber,

        @NotNull
        SeatType seatType,

        @NotNull
        @Positive
        Integer rowNumber,

        @NotNull
        @Positive
        Integer columnNumber,

        boolean active
) {
}