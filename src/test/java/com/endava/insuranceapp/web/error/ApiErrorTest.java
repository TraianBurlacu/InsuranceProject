package com.endava.insuranceapp.web.error;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void apiError_getters_work_and_values_are_present() {
        OffsetDateTime now = OffsetDateTime.now();
        ApiError e = new ApiError(now, 400, "Bad Request", "Something went wrong", "/path");

        assertThat(e.timestamp()).isEqualTo(now);
        assertThat(e.status()).isEqualTo(400);
        assertThat(e.error()).isEqualTo("Bad Request");
        assertThat(e.message()).isEqualTo("Something went wrong");
        assertThat(e.path()).isEqualTo("/path");
    }
}
