package com.lsf.ironbus.train.web;

import com.lsf.ironbus.train.app.*;
import com.lsf.ironbus.train.app.command.AddCoachCommand;
import com.lsf.ironbus.train.app.command.CreateTrainCommand;
import com.lsf.ironbus.train.app.response.CoachResponse;
import com.lsf.ironbus.train.app.response.TrainResponse;
import com.lsf.ironbus.train.app.service.AddCoachService;
import com.lsf.ironbus.train.app.service.AdminTrainService;
import com.lsf.ironbus.train.app.service.CreateTrainService;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.web.controller.AdminTrainController;
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

@WebMvcTest(AdminTrainController.class)
class AdminTrainControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean
    CreateTrainService createTrainService;
    @MockitoBean
    AddCoachService addCoachService;

    @MockitoBean
    AdminTrainService  adminTrainService;

    @Test
    void createsTrain() throws Exception {
        UUID id = UUID.randomUUID();
        when(createTrainService.create(any(CreateTrainCommand.class)))
                .thenReturn(new TrainResponse(id, "UDR-001", "Udarata Menike", true));
        mockMvc.perform(post("/api/v1/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"UDR-001","name":"Udarata Menike", "active":true}
                                """
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("UDR-001"));
    }

    @Test
    void addsCoach() throws Exception {
        UUID trainId = UUID.randomUUID();
        when(addCoachService.add(any(AddCoachCommand.class)))
                .thenReturn(new CoachResponse(UUID.randomUUID(), trainId, "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, true));
        mockMvc.perform(post("/api/v1/admin/trains/{trainId}/coaches", trainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"coachNumber":"R1","travelClass":"SECOND_CLASS","reservationMode":"RESERVED", "active":true}
                                """
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coachNumber").value("R1"));
    }
}
