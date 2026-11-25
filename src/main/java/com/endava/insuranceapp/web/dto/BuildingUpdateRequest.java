package com.endava.insuranceapp.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BuildingUpdateRequest(
        @NotNull(message = "cityId is required")
        Long cityId,

        @NotBlank(message = "street is required")
        @Size(max = 200, message = "street must be max 200 chars")
        String street,

        @NotBlank(message = "number is required")
        @Size(max = 20, message = "number must be max 20 chars")
        String number,

        Integer constructionYear,

        @NotNull(message = "buildingType is required")
        String buildingType,

        Integer floors,

        @NotNull(message = "surfaceArea is required")
        @DecimalMin(value = "0.01", message = "surfaceArea must be > 0")
        BigDecimal surfaceArea,

        @NotNull(message = "insuredValue is required")
        @DecimalMin(value = "0.01", message = "insuredValue must be > 0")
        BigDecimal insuredValue,

        @Size(max = 500, message = "riskIndicators must be max 500 chars")
        String riskIndicators
) {}
