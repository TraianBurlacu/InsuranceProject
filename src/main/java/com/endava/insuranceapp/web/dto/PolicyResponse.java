package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PolicyResponse(
        Long id,
        String policyNumber,
        Long clientId,
        Long buildingId,
        Long brokerId,
        String brokerCode,
        String currencyCode,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal basePremium,
        BigDecimal finalPremium,
        String cancellationReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
