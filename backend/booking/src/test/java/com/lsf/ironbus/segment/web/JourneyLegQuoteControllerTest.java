package com.lsf.ironbus.segment.web;

import com.lsf.ironbus.segment.app.response.JourneyLegQuoteResponse;
import com.lsf.ironbus.segment.app.service.JourneyLegQuoteService;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.web.controller.JourneyLegQuoteController;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JourneyLegQuoteController.class)
class JourneyLegQuoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JourneyLegQuoteService quoteService;

    @Test
    void returnsJourneyLegQuote() throws Exception {
        UUID journeyId = UUID.randomUUID();
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        when(quoteService.quote(
                any(ResolveJourneyLegCommand.class),
                eq(TravelClass.SECOND_CLASS)
        )).thenReturn(new JourneyLegQuoteResponse(
                journeyId,
                originId,
                destinationId,
                0,
                2,
                List.of(0, 1),
                new BigDecimal("120.00"),
                TravelClass.SECOND_CLASS,
                new BigDecimal("1300.00"),
                "LKR"
        ));

        mockMvc.perform(get(
                        "/api/v1/journeys/{journeyId}/quote",
                        journeyId
                )
                        .param("originStationId", originId.toString())
                        .param("destinationStationId", destinationId.toString())
                        .param("travelClass", "SECOND_CLASS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.segmentSequences[0]").value(0))
                .andExpect(jsonPath("$.segmentSequences[1]").value(1))
                .andExpect(jsonPath("$.distanceKm").value(120.00))
                .andExpect(jsonPath("$.fareAmount").value(1300.00))
                .andExpect(jsonPath("$.currency").value("LKR"));
    }

    @Test
    void rejectsInvalidTravelClass() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/journeys/{journeyId}/quote",
                        UUID.randomUUID()
                )
                        .param("originStationId", UUID.randomUUID().toString())
                        .param("destinationStationId", UUID.randomUUID().toString())
                        .param("travelClass", "ECONOMY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMalformedJourneyId() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/journeys/{journeyId}/quote",
                        "not-a-uuid"
                )
                        .param("originStationId", UUID.randomUUID().toString())
                        .param("destinationStationId", UUID.randomUUID().toString())
                        .param("travelClass", "SECOND_CLASS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingQueryParameter() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/journeys/{journeyId}/quote",
                        UUID.randomUUID()
                )
                        .param("originStationId", UUID.randomUUID().toString())
                        .param("travelClass", "SECOND_CLASS"))
                .andExpect(status().isBadRequest());
    }
}
