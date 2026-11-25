package com.endava.insuranceapp.web.logging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters (NOT your custom filters)
class LoggingFiltersIT {

    @Autowired MockMvc mvc;

    @Test
    void requestGetsTraceIdHeader() throws Exception {
        mvc.perform(get("/__test/ping"))
                .andExpect(status().isOk())
                .andExpect(header().string(CorrelationIdFilter.HEADER, notNullValue()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/__test/ping")
        ResponseEntity<String> ping() {
            return ResponseEntity.ok("ok");
        }
    }
}
