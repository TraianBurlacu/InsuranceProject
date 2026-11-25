package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientBuildingFlowIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createClient_createBuilding_getBuildingDetails_happyPath() throws Exception {
        // Create client
        String clientJson = """
      {
        "type": "INDIVIDUAL",
        "name": "John Doe",
        "identifier": "CNP-123456",
        "email": "john@doe.com",
        "phone": "0712345678",
        "address": "Somewhere",
        "primaryAddress": ""
      }
      """;

        MvcResult clientResult = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        Long clientId = JsonPath.parse(clientResult.getResponse().getContentAsString()).read("$.id", Long.class);

        // Create building for client
        Long cityId = 1L;

        String buildingJson = """
      {
        "cityId": %d,
        "street": "Eroilor",
        "number": "10A",
        "constructionYear": 2005,
        "buildingType": "RESIDENTIAL",
        "floors": 4,
        "surfaceArea": 120.5,
        "insuredValue": 250000,
        "riskIndicators": "near river"
      }
      """.formatted(cityId);

        MvcResult buildingResult = mockMvc.perform(post("/api/brokers/clients/" + clientId + "/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.cityId").value(1))
                .andExpect(jsonPath("$.cityName", not(emptyOrNullString())))
                .andReturn();

        Long buildingId = JsonPath.parse(buildingResult.getResponse().getContentAsString()).read("$.id", Long.class);

        // Get building details
        mockMvc.perform(get("/api/brokers/buildings/" + buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(buildingId))
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.countryName").value("Romania"))
                .andExpect(jsonPath("$.street").value("Eroilor"))
                .andExpect(jsonPath("$.insuredValue").value(250000));
    }
}
