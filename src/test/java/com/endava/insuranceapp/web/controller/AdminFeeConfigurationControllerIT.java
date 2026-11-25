package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AdminFeeConfigurationControllerIT {

    @Autowired MockMvc mockMvc;

    @Test
    void create_thenList_works() throws Exception {
        String createJson = """
      {
        "name": "Earthquake fee",
        "type": "RiskAdjustment",
        "percentage": 0.05,
        "effectiveFrom": "2026-01-01",
        "effectiveTo": null,
        "isActive": true
      }
      """;

        MvcResult created = mockMvc.perform(post("/api/admin/fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        Long id = JsonPath.parse(created.getResponse().getContentAsString()).read("$.id", Long.class);

        mockMvc.perform(get("/api/admin/fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id==" + id + ")].name").isNotEmpty());
    }
}
