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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientSearchPagingIT {

    @Autowired
    MockMvc mockMvc;

    private String clientJson(String type, String name, String identifier) {
        return """
      {
        "type": "%s",
        "name": "%s",
        "identifier": "%s",
        "email": "%s@example.com",
        "phone": "0712345678",
        "address": "Addr",
        "primaryAddress": ""
      }
      """.formatted(type, name, identifier, identifier.toLowerCase());
    }

    @Test
    void searchClients_byName_withPaging_returnsPagedResults() throws Exception {
        // create 2 clients with name containing "Alice" and 1 unrelated client
        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson("INDIVIDUAL", "Alice", "ID-A1")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson("INDIVIDUAL", "Alice B", "ID-A2")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson("INDIVIDUAL", "Bob", "ID-B1")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/brokers/clients")
                        .param("name", "Alice")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].name", containsString("Alice")));
    }
}
