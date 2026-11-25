package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeeConfigurationResponse(
        Long id,
        String name,
        String type,
        BigDecimal percentage,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        boolean isActive
) {}
