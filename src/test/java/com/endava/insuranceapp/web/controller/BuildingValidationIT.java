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
@AutoConfigureMockMvc
class BuildingValidationIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createBuilding_negativeInsuredValue_returns400() throws Exception {
        // clientId doesn't matter; validation should fail before service is called
        String json = """
      {
        "cityId": 1,
        "street": "Eroilor",
        "number": "10",
        "constructionYear": 2000,
        "buildingType": "RESIDENTIAL",
        "floors": 2,
        "surfaceArea": 100,
        "insuredValue": -5,
        "riskIndicators": "none"
      }
      """;

        mockMvc.perform(post("/api/brokers/clients/1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
