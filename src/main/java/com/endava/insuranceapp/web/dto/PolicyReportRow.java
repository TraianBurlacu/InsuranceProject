package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;

public record PolicyReportRow(
        String groupKey,          // country/county/city/broker name (or code)
        String currency,          // currency code
        long policyCount,
        BigDecimal totalFinalPremium,
        BigDecimal totalFinalPremiumInBaseCurrency
) {}
