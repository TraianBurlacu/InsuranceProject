package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeeConfigurationCreateRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200, message = "name must be max 200 chars")
        String name,

        @NotBlank(message = "type is required")
        @Size(max = 50, message = "type must be max 50 chars")
        String type,

        @NotNull(message = "percentage is required")
        @DecimalMin(value = "-0.999999", message = "percentage must be >= -0.999999")
        @DecimalMax(value = "10.000000", message = "percentage must be <= 10.000000")
        BigDecimal percentage,

        @NotNull(message = "effectiveFrom is required")
        LocalDate effectiveFrom,

        LocalDate effectiveTo,

        @NotNull(message = "isActive is required")
        Boolean isActive
) {}
