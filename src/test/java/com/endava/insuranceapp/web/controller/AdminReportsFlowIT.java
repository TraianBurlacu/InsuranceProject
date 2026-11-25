package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AdminReportsFlowIT {

    @Autowired MockMvc mockMvc;

    @Test
    void fullFlow_createClientBuildingPolicy_activate_thenReportByCity() throws Exception {
        // Admin creates broker
        String brokerCreateJson = """
        {
          "brokerCode": "BRK-REPORT-1",
          "name": "Report Broker",
          "email": "report.broker@example.com",
          "phone": "0710000000",
          "isActive": true,
          "commissionPercentage": null
        }
        """;

        MvcResult brokerCreated = mockMvc.perform(post("/api/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokerCreateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brokerCode", is("BRK-REPORT-1")))
                .andReturn();

        Long brokerId = JsonPath.parse(brokerCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Broker creates client
        String clientCreateJson = """
        {
          "type": "INDIVIDUAL",
          "name": "John Report",
          "identifier": "CNP-REPORT-001",
          "email": "john.report@example.com",
          "phone": "0720000000",
          "address": "Street 1",
          "primaryAddress": null
        }
        """;

        MvcResult clientCreated = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientCreateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier", is("CNP-REPORT-001")))
                .andReturn();

        Long clientId = JsonPath.parse(clientCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Broker creates building for client
        long cityId = 1L;
        String buildingCreateJson = """
        {
          "type": "RESIDENTIAL",
          "insuredValue": 100000,
          "cityId": %d
        }
        """.formatted(cityId);

        MvcResult buildingCreated = mockMvc.perform(post("/api/brokers/clients/" + clientId + "/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildingCreateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cityId", is((int) cityId)))
                .andReturn();

        Long buildingId = JsonPath.parse(buildingCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Broker creates draft policy
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = start.plusMonths(6);

        String policyCreateJson = """
        {
          "clientId": %d,
          "buildingId": %d,
          "brokerCode": "BRK-REPORT-1",
          "currencyCode": "RON",
          "basePremium": 1000.00,
          "startDate": "%s",
          "endDate": "%s"
        }
        """.formatted(clientId, buildingId, start, end);

        MvcResult policyCreated = mockMvc.perform(post("/api/brokers/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(policyCreateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andReturn();

        Long policyId = JsonPath.parse(policyCreated.getResponse().getContentAsString()).read("$.id", Long.class);

        // Activate policy
        mockMvc.perform(post("/api/brokers/policies/" + policyId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        // Admin calls report by city
        mockMvc.perform(get("/api/admin/reports/policies-by-city")
                        .param("status", "ACTIVE")
                        .param("from", start.minusDays(1).toString())
                        .param("to", start.plusDays(1).toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].currency", notNullValue()))
                .andExpect(jsonPath("$[0].policyCount", greaterThanOrEqualTo(1)));
    }
}
