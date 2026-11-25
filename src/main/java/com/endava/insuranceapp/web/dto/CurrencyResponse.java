package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;

public record CurrencyResponse(
        Long id,
        String code,
        String name,
        BigDecimal exchangeRateToBase,
        boolean isActive
) {}
