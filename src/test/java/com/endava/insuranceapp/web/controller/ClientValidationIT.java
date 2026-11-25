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
class ClientValidationIT {

    @Autowired MockMvc mockMvc;

    @Test
    void createClient_duplicateIdentifier_returns400() throws Exception {
        String json = """
      {
        "type": "INDIVIDUAL",
        "name": "John",
        "identifier": "DUP-1",
        "email": "john@doe.com",
        "phone": "07",
        "address": "Addr",
        "primaryAddress": ""
      }
      """;

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/brokers/clients")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
