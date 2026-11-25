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
@AutoConfigureMockMvc(addFilters = false)
class AdminBrokerControllerIT {

    @Autowired MockMvc mockMvc;

    @Test
    void create_update_activate_deactivate_flow() throws Exception {
        Long id = createBrokerAndReturnId("BRK-99", "Test Broker");

        String updateJson = """
      {
        "name": "Test Broker Updated",
        "email": "tb2@example.com",
        "phone": "0720000000",
        "isActive": false,
        "commissionPercentage": null
      }
      """;

        mockMvc.perform(put("/api/admin/brokers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Broker Updated")))
                .andExpect(jsonPath("$.isActive", is(false)));

        mockMvc.perform(post("/api/admin/brokers/" + id + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(true)));

        mockMvc.perform(post("/api/admin/brokers/" + id + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)));
    }

    @Test
    void getById_returnsBroker() throws Exception {
        Long id = createBrokerAndReturnId("BRK-101", "Broker 101");

        mockMvc.perform(get("/api/admin/brokers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.brokerCode", is("BRK-101")))
                .andExpect(jsonPath("$.name", is("Broker 101")));
    }

    @Test
    void listAll_returnsArrayContainingCreatedBrokers() throws Exception {
        createBrokerAndReturnId("BRK-201", "Broker 201");
        createBrokerAndReturnId("BRK-202", "Broker 202");

        mockMvc.perform(get("/api/admin/brokers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$[*].brokerCode", hasItems("BRK-201", "BRK-202")));
    }

    @Test
    void getById_whenMissing_returns404() throws Exception {
        mockMvc.perform(get("/api/admin/brokers/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.path", is("/api/admin/brokers/999999")));
    }


    private Long createBrokerAndReturnId(String brokerCode, String name) throws Exception {
        String createJson = """
      {
        "brokerCode": "%s",
        "name": "%s",
        "email": "%s@example.com",
        "phone": "0710000000",
        "isActive": true,
        "commissionPercentage": null
      }
      """.formatted(brokerCode, name, brokerCode.toLowerCase());

        MvcResult created = mockMvc.perform(post("/api/admin/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brokerCode", is(brokerCode)))
                .andReturn();

        return JsonPath.parse(created.getResponse().getContentAsString()).read("$.id", Long.class);
    }
}
