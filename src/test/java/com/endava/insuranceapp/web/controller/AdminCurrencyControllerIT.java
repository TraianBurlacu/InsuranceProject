package com.endava.insuranceapp.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AdminCurrencyControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void list_returnsSeededCurrencies() throws Exception {
        mockMvc.perform(get("/api/admin/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[*].code", hasItem("RON")))
                .andExpect(jsonPath("$[*].code", hasItem("EUR")));
    }

    @Test
    void create_thenUpdate_works() throws Exception {
        String createJson = """
      {
        "code": "USD",
        "name": "US Dollar",
        "exchangeRateToBase": 4.100000,
        "isActive": true
      }
      """;

        MvcResult created = mockMvc.perform(post("/api/admin/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code", is("USD")))
                .andReturn();

        Long id = JsonPath.parse(created.getResponse().getContentAsString()).read("$.id", Long.class);

        String updateJson = """
      {
        "name": "US Dollar Updated",
        "exchangeRateToBase": 4.200000,
        "isActive": false
      }
      """;

        mockMvc.perform(put("/api/admin/currencies/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.code").value("USD"))
                .andExpect(jsonPath("$.name").value("US Dollar Updated"))
                .andExpect(jsonPath("$.exchangeRateToBase").value(4.2))
                .andExpect(jsonPath("$.isActive").value(false));
    }
}
