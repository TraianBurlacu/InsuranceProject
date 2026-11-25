package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RiskFactorUpdateRequest(
        @NotBlank(message = "level is required")
        @Size(max = 30, message = "level must be max 30 chars")
        String level,

        Long referenceId,

        @Size(max = 30, message = "buildingType must be max 30 chars")
        String buildingType,

        @NotNull(message = "adjustmentPercentage is required")
        @DecimalMin(value = "-0.999999", message = "adjustmentPercentage must be >= -0.999999")
        @DecimalMax(value = "10.000000", message = "adjustmentPercentage must be <= 10.000000")
        BigDecimal adjustmentPercentage,

        @NotNull(message = "isActive is required")
        Boolean isActive
) {}
