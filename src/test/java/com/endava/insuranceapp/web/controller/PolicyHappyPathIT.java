package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PolicyHappyPathIT {

    @Autowired MockMvc mockMvc;

    @Test
    void createActivateFetch_policyHappyPath() throws Exception {
        // create broker
        String brokerJson = """
      {
          "brokerCode": "BRK-POL",
          "name": "Broker Pol",
          "email": "b@p.com",
          "phone": "07",
          "isActive": true,
          "commissionPercentage": null
      }
      """;
        MvcResult br = mockMvc.perform(post("/api/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON).content(brokerJson))
                .andExpect(status().isCreated()).andReturn();
        Long brokerId = JsonPath.parse(br.getResponse().getContentAsString()).read("$.id", Long.class);

        // create client
        String clientJson = """
      {
          "type": "INDIVIDUAL",
          "name": "Policy Client"
          ,"identifier": "POL-CL-2",
          "email": "pc@a.com",
          "phone": "07",
          "address": "Addr",
          "primaryAddress": ""
      }
      """;
        MvcResult cr = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON).content(clientJson))
                .andExpect(status().isCreated()).andReturn();
        Long clientId = JsonPath.parse(cr.getResponse().getContentAsString()).read("$.id", Long.class);

        // create building
        String buildingJson = """
      {
          "cityId": 1,
          "street": "Main",
          "number": "1",
          "constructionYear": 2000,
          "buildingType": "RESIDENTIAL",
          "floors": 1,
          "surfaceArea": 50,
          "insuredValue": 100000,
          "riskIndicators": "none"
      }
      """;
        MvcResult brg = mockMvc.perform(post("/api/brokers/clients/" + clientId + "/buildings")
                        .contentType(MediaType.APPLICATION_JSON).content(buildingJson))
                .andExpect(status().isCreated()).andReturn();
        Long buildingId = JsonPath.parse(brg.getResponse().getContentAsString()).read("$.id", Long.class);

        // create policy draft
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusYears(1);
        String policyJson = String.format("""
          {
            "clientId": %d,
            "buildingId": %d,
            "brokerCode": "BRK-POL",
            "currencyCode": "RON",
            "basePremium": 1000,
            "startDate": "%s",
            "endDate": "%s"
          }
          """, clientId, buildingId, start, end);

        MvcResult pCreated = mockMvc.perform(post("/api/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON).content(policyJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        Long policyId = JsonPath.parse(pCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // activate policy
        mockMvc.perform(post("/api/brokers/policies/" + policyId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // fetch and assert
        mockMvc.perform(get("/api/brokers/policies/" + policyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
