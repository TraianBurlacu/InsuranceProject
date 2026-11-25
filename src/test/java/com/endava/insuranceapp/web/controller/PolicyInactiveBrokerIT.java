package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PolicyInactiveBrokerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createPolicy_withInactiveBroker_returns400() throws Exception {
        // Create broker
        String brokerJson = """
      {
        "brokerCode": "BRK-INACTIVE",
        "name": "Inactive Broker",
        "email": "inactive@broker.com",
        "phone": "0700000000",
        "isActive": true,
        "commissionPercentage": null
      }
      """;

        MvcResult brokerCreated = mockMvc.perform(post("/api/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokerJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long brokerId = JsonPath.parse(brokerCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Deactivate broker
        mockMvc.perform(post("/api/admin/brokers/" + brokerId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)));

        // Create client
        String clientJson = """
      {
        "type": "INDIVIDUAL",
        "name": "John Policy",
        "identifier": "POL-CL-1",
        "email": "john@p.com",
        "phone": "0712345678",
        "address": "Addr",
        "primaryAddress": ""
      }
      """;

        MvcResult clientCreated = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long clientId = JsonPath.parse(clientCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Create building for that client
        String buildingJson = """
      {
        "cityId": 1,
        "street": "Eroilor",
        "number": "10",
        "constructionYear": 2000,
        "buildingType": "RESIDENTIAL",
        "floors": 2,
        "surfaceArea": 100,
        "insuredValue": 200000,
        "riskIndicators": "none"
      }
      """;

        MvcResult buildingCreated = mockMvc.perform(post("/api/brokers/clients/" + clientId + "/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long buildingId = JsonPath.parse(buildingCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Attempt to create a policy with inactive broker
        String policyJson = """
          {
            "clientId": %d,
            "buildingId": %d,
            "brokerCode": "BRK-INACTIVE",
            "currencyCode": "RON",
            "basePremium": 1000,
            "startDate": "2026-02-10",
            "endDate": "2027-02-10"
          }
          """.formatted(clientId, buildingId);

        mockMvc.perform(post("/api/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(policyJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Broker")))
                .andExpect(jsonPath("$.path").value("/api/brokers/policies"));
    }
}
