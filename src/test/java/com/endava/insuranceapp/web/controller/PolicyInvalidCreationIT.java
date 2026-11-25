package com.endava.insuranceapp.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PolicyInvalidCreationIT {

    @Autowired MockMvc mockMvc;

    @Test
    void createPolicy_negativePremium_returns400() throws Exception {
        String json = """
      {
        "clientId": 9999,
        "buildingId": 9999,
        "brokerId": 9999,
        "currencyCode": "RON",
        "basePremium": -100,
        "startDate": "2026-01-01",
        "endDate": "2027-01-01"
      }
      """;

        mockMvc.perform(post("/api/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }
}
