package com.endava.insuranceapp.web.error;

import com.endava.insuranceapp.web.controller.TestExceptionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestExceptionController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerSliceTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void handleNotFound_isExecuted() throws Exception {
        mockMvc.perform(get("/test/error/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("test resource missing")))
                .andExpect(jsonPath("$.path", is("/test/error/notfound")));
    }

    @Test
    void handleBadRequest_isExecuted() throws Exception {
        mockMvc.perform(get("/test/error/badrequest"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("bad argument")))
                .andExpect(jsonPath("$.path", is("/test/error/badrequest")));
    }

    @Test
    void handleGeneric_isExecuted() throws Exception {
        mockMvc.perform(get("/test/error/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", containsString("Unexpected error")))
                .andExpect(jsonPath("$.path", is("/test/error/generic")));
    }

    @Test
    void handleValidation_isExecuted() throws Exception {
        String json = "{\"name\": \"\"}";

        mockMvc.perform(post("/test/error/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("name required")))
                .andExpect(jsonPath("$.path", is("/test/error/validation")));
    }
}
