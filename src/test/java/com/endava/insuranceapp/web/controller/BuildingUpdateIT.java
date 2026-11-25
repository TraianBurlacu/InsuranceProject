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
@AutoConfigureMockMvc
class BuildingUpdateIT {

    @Autowired MockMvc mockMvc;

    @Test
    void updateBuilding_happyPath_keepsClientId() throws Exception {
        // create client
        String clientJson = """
      {
          "type": "INDIVIDUAL",
          "name": "A",
          "identifier": "U-CL-1",
          "email": "a@a.com",
          "phone": "07",
          "address": "x",
          "primaryAddress": ""
      }
      """;
        MvcResult clientRes = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON).content(clientJson))
                .andExpect(status().isCreated()).andReturn();
        Long clientId = JsonPath.parse(clientRes.getResponse().getContentAsString()).read("$.id", Long.class);

        // create building
        String buildingJson = """
      {
          "cityId": 1,
          "street": "S",
          "number": "1",
          "constructionYear": 2000,
          "buildingType": "RESIDENTIAL",
          "floors": 1,
          "surfaceArea": 10,
          "insuredValue": 1000,
          "riskIndicators": "n/a"
       }
      """;
        MvcResult bRes = mockMvc.perform(post("/api/brokers/clients/" + clientId + "/buildings")
                        .contentType(MediaType.APPLICATION_JSON).content(buildingJson))
                .andExpect(status().isCreated()).andReturn();
        Long buildingId = JsonPath.parse(bRes.getResponse().getContentAsString()).read("$.id", Long.class);

        // update building
        String updateJson = """
      {
          "cityId": 1,
          "street": "S2",
          "number": "2",
          "constructionYear": 2010,
          "buildingType": "OFFICE",
          "floors": 2,
          "surfaceArea": 20,
          "insuredValue": 2000,
          "riskIndicators": "none"
          }
      """;

        mockMvc.perform(put("/api/brokers/buildings/" + buildingId)
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(buildingId))
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.street").value("S2"))
                .andExpect(jsonPath("$.buildingType").value("OFFICE"));
    }
}
