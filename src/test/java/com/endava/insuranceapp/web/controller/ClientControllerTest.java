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
class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void create_getById_update_search_happyPath() throws Exception {
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

        MvcResult createResult = mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.identifier").value("CNP-123456"))
                .andReturn();

        Long clientId = JsonPath.parse(createResult.getResponse().getContentAsString())
                .read("$.id", Long.class);

        mockMvc.perform(get("/api/brokers/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.identifier").value("CNP-123456"));

        String updateJson = """
        {
          "name": "John Updated",
          "email": "john.updated@doe.com",
          "phone": "0799999999",
          "address": "New Address",
          "primaryAddress": ""
        }
        """;

        mockMvc.perform(put("/api/brokers/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@doe.com"))
                .andExpect(jsonPath("$.phone").value("0799999999"))
                .andExpect(jsonPath("$.address").value("New Address"));

        mockMvc.perform(get("/api/brokers/clients")
                        .param("name", "John")
                        .param("identifier", "CNP-123456")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // Spring Page JSON structure
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.content", is(not(empty()))))
                .andExpect(jsonPath("$.content[*].id", hasItem(clientId.intValue())))
                .andExpect(jsonPath("$.content[*].identifier", hasItem("CNP-123456")));

        mockMvc.perform(get("/api/brokers/clients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void create_shouldReturn400_whenValidationFails() throws Exception {
        String invalidClientJson = """
        {
          "type": "INDIVIDUAL",
          "name": "",
          "identifier": "",
          "email": "not-an-email"
        }
        """;

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidClientJson))
                .andExpect(status().isBadRequest());
    }
}
