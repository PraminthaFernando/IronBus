package com.lsf.ironbus.train.web.request;

import com.lsf.ironbus.train.enums.SeatType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record BulkCreateSeatsRequest(

        @Min(1)
        @Max(100)
        int rows,

        @NotEmpty
        List<
                @Pattern(regexp = "^[A-Z0-9]+$")
                        String
                > columnSuffixes,

        @NotNull
        SeatType seatType
) {
}