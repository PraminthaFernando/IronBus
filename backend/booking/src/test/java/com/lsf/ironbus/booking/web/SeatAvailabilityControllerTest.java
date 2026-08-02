package com.lsf.ironbus.booking.web;

import com.lsf.ironbus.booking.app.response.AvailabilityResponse;
import com.lsf.ironbus.booking.app.response.AvailableSeatResponse;
import com.lsf.ironbus.booking.app.service.FindAvailableSeatsService;
import com.lsf.ironbus.booking.web.controller.SeatAvailabilityController;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatAvailabilityController.class)
class SeatAvailabilityControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    FindAvailableSeatsService service;

    @Test
    void returnsAvailableSeats() throws Exception {
        UUID j = UUID.randomUUID(), o = UUID.randomUUID(), d = UUID.randomUUID(), s = UUID.randomUUID(), c = UUID.randomUUID();
        when(service
                .find(any())
            )
            .thenReturn(
                new AvailabilityResponse(
                    j, o, d, 0, 2,
                    new BigDecimal("120.00"),
                    List.of(0, 1),
                    List.of(
                        new AvailableSeatResponse(
                            s, c, "R1",
                            TravelClass.SECOND_CLASS,
                            "1A",
                            SeatType.WINDOW,
                            1, 1,
                            new BigDecimal("1300.00"),
                            "LKR"
                        )
                    )
                )
            );

        mvc.perform(
            get(
                "/api/v1/journeys/{journeyId}/available-seats",
                j
            )
            .param(
                "originStationId",
                o.toString()
            )
            .param(
                "destinationStationId",
                d.toString()
            )
        )
        .andExpect(
            status()
                .isOk()
        )
            .andExpect(
                jsonPath(
                    "$.seats[0].seatId"
                )
                    .value(s.toString())
            )
            .andExpect(
                jsonPath("$.fareAmount")
                    .doesNotExist()
            );
    }

    @Test
    void rejectsMalformedUuid() throws Exception {
        mvc.perform(
            get(
                "/api/v1/journeys/{journeyId}/available-seats",
                "bad"
            )
            .param(
                "originStationId",
                UUID.randomUUID().toString()
            )
            .param(
                "destinationStationId",
                UUID.randomUUID().toString()
            )
        )
        .andExpect(status().isBadRequest());
    }
}
