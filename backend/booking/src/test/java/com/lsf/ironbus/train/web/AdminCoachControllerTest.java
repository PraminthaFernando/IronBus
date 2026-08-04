package com.lsf.ironbus.train.web;

import com.lsf.ironbus.train.app.*;
import com.lsf.ironbus.train.app.command.AddSeatCommand;
import com.lsf.ironbus.train.app.response.SeatResponse;
import com.lsf.ironbus.train.app.service.AddSeatService;
import com.lsf.ironbus.train.app.service.AdminCoachService;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.web.controller.AdminCoachController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCoachController.class)
class AdminCoachControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean
    AddSeatService addSeatService;

    @MockitoBean
    AdminCoachService adminCoachService;

    @Test
    void addsSeat() throws Exception {
        UUID coachId = UUID.randomUUID();
        when(addSeatService.add(any(AddSeatCommand.class)))
                .thenReturn(new SeatResponse(UUID.randomUUID(), coachId, "1A", SeatType.WINDOW, 1, 1, true));
        mockMvc.perform(post("/api/v1/admin/coaches/{coachId}/seats", coachId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"seatNumber":"1A","seatType":"WINDOW","rowNumber":1,"columnNumber":1, "active":true}
                                """
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value("1A"));
    }
}
