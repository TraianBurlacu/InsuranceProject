package com.endava.insuranceapp.application.service.report;

import java.time.LocalDate;

public record ReportFilter(
        LocalDate from,
        LocalDate to,
        String status,       // "ACTIVE" | "EXPIRED" | "CANCELLED" or null
        String currency,
        String buildingType
) {}
