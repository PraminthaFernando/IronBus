package com.lsf.ironbus.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "ironbus.cors.allowed-origins[0]=http://localhost:3000"
})
@AutoConfigureMockMvc
class CorsConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowConfiguredFrontendOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/routes")
            .header("Origin", "http://localhost:3000")
            .header(
                "Access-Control-Request-Method",
                "GET"
            ))
            .andExpect(status().isOk())
            .andExpect(header().string(
                "Access-Control-Allow-Origin",
                "http://localhost:3000"
            ));
    }

    @Test
    void shouldNotAllowUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/routes")
            .header("Origin", "https://untrusted.example")
            .header(
                "Access-Control-Request-Method",
                "GET"
            ))
            .andExpect(status().isForbidden());
    }
}