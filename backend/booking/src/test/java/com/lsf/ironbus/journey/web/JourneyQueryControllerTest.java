package com.lsf.ironbus.journey.web;

import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.FindJourneysService;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.web.controller.JourneyQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JourneyQueryController.class)
class JourneyQueryControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean
    FindJourneysService findJourneysService;

    @Test
    void findsJourneysByRouteAndDate() throws Exception {
        UUID journeyId = UUID.randomUUID(), trainId = UUID.randomUUID(), routeId = UUID.randomUUID();
        when(findJourneysService.find(eq(routeId), eq(LocalDate.of(2026, 8, 2)), eq(ZoneId.of("Asia/Colombo"))))
                .thenReturn(List.of(new JourneyResponse(journeyId, trainId, "UDR-001", routeId, "FOT-BAD",
                        Instant.parse("2026-08-02T00:00:00Z"), JourneyStatus.SCHEDULED)));
        mockMvc.perform(get("/api/v1/journeys")
                        .param("routeId", routeId.toString())
                        .param("date", "2026-08-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(journeyId.toString()));
    }
}
