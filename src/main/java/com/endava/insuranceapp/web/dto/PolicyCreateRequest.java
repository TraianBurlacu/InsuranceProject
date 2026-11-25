package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyCreateRequest(
        @NotNull(message = "clientId is required") Long clientId,
        @NotNull(message = "buildingId is required") Long buildingId,

        @NotBlank(message = "brokerCode is required")
        @Size(max = 50, message = "brokerCode must be max 50 chars")
        String brokerCode,

        @NotBlank(message = "currencyCode is required")
        @Size(max = 10, message = "currencyCode must be max 10 chars")
        String currencyCode,

        @NotNull(message = "basePremium is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "basePremium must be > 0")
        BigDecimal basePremium,

        @NotNull(message = "startDate is required") LocalDate startDate,
        @NotNull(message = "endDate is required") LocalDate endDate
) {}
