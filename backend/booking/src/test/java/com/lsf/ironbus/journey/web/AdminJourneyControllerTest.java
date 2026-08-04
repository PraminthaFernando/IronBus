package com.lsf.ironbus.journey.web;

import com.lsf.ironbus.journey.app.command.ScheduleJourneyCommand;
import com.lsf.ironbus.journey.app.response.JourneyResponse;
import com.lsf.ironbus.journey.app.service.AdminJourneyService;
import com.lsf.ironbus.journey.app.service.ScheduleJourneyService;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.web.controller.AdminJourneyController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminJourneyController.class)
class AdminJourneyControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean
    ScheduleJourneyService scheduleJourneyService;

    @MockitoBean
    AdminJourneyService adminJourneyService;

    @Test
    void schedulesJourney() throws Exception {
        UUID journeyId = UUID.randomUUID(), trainId = UUID.randomUUID(), routeId = UUID.randomUUID();
        when(scheduleJourneyService.schedule(any(ScheduleJourneyCommand.class)))
                .thenReturn(new JourneyResponse(journeyId, trainId, "UDR-001", routeId, "FOT-BAD",
                        Instant.parse("2027-01-01T00:00:00Z"), JourneyStatus.SCHEDULED));
        mockMvc.perform(post("/api/v1/admin/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"trainId":"%s","routeId":"%s","departureTime":"2027-01-01T00:00:00Z"}
                                """.formatted(trainId, routeId)
                        ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }
}
