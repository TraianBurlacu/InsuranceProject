package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;

public record RiskFactorResponse(
        Long id,
        String level,
        Long referenceId,
        String buildingType,
        BigDecimal adjustmentPercentage,
        boolean isActive
) {}
