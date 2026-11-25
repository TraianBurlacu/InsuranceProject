package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BrokerResponse(
        Long id,
        String brokerCode,
        String name,
        String email,
        String phone,
        boolean isActive,
        BigDecimal commissionPercentage,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
