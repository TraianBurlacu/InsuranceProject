package com.endava.insuranceapp.web.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GeographyControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getCountries_returnsSeededCountries() throws Exception {
        mockMvc.perform(get("/api/brokers/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[*].name", hasItem("Romania")));
    }

    @Test
    void getCountiesByCountry_whenCountryMissing_returns404WithApiError() throws Exception {
        mockMvc.perform(get("/api/brokers/countries/999999/counties"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Country with id")))
                .andExpect(jsonPath("$.path", is("/api/brokers/countries/999999/counties")));
    }
}
