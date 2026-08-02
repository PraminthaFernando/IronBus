package com.lsf.ironbus.booking.web;

import com.lsf.ironbus.booking.app.response.BookingResponse;
import com.lsf.ironbus.booking.app.service.CancelBookingService;
import com.lsf.ironbus.booking.app.service.CreateBookingService;
import com.lsf.ironbus.booking.app.service.GetBookingService;
import com.lsf.ironbus.booking.enums.BookingStatus;
import com.lsf.ironbus.booking.web.controller.BookingController;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    CreateBookingService create;

    @MockitoBean
    GetBookingService get;

    @MockitoBean
    CancelBookingService cancel;

    private BookingResponse resp() {
        return new BookingResponse(
            UUID.randomUUID(),
            "LSF-26-ABC123",
            BookingStatus.CONFIRMED,
            UUID.randomUUID(),
            Instant.parse("2026-08-05T00:00:00Z"),
            UUID.randomUUID(),
            "R1",
            "1A",
            TravelClass.SECOND_CLASS,
            "FOT",
            "Colombo Fort",
            "KDT",
            "Kandy",
            new BigDecimal("1300.00"),
            "LKR",
            Instant.parse("2026-08-01T05:00:00Z")
        );
    }

    @Test
    void creates() throws Exception {
        when(create.create(any())).thenReturn(resp());
        mvc.perform(
            post("/api/v1/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "journeyId":"%s",
                    "seatId":"%s",
                    "originStationId":"%s",
                    "destinationStationId":"%s",
                    "passenger":{
                        "name":"A",
                        "email":"a@b.com",
                        "phone":"+94"
                    }
                }"""
                .formatted(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID()
                )
            )
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status")
            .value("CONFIRMED")
        );
    }

    @Test
    void rejectsBadEmail() throws Exception {
        mvc.perform(
            post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "journeyId":"%s",
                            "seatId":"%s",
                            "originStationId":"%s",
                            "destinationStationId":"%s",
                            "passenger":{
                                "name":"A",
                                "email":"bad",
                                "phone":"+94"
                            }
                        }"""
                        .formatted(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            UUID.randomUUID()
                        )
                    )
        ).andExpect(status().isBadRequest());
    }
}
