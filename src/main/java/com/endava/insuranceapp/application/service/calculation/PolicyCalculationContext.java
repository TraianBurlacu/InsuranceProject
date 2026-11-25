package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.BuildingEntity;

import java.time.LocalDate;

public record PolicyCalculationContext(
        LocalDate effectiveDate,
        BuildingEntity building,
        BrokerEntity broker
) {}