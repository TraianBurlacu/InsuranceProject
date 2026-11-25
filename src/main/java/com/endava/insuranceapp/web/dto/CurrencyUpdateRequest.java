package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CurrencyUpdateRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be max 100 chars")
        String name,

        @NotNull(message = "exchangeRateToBase is required")
        @DecimalMin(value = "0.000001", message = "exchangeRateToBase must be > 0")
        BigDecimal exchangeRateToBase,

        @NotNull(message = "isActive is required")
        Boolean isActive
) {}
