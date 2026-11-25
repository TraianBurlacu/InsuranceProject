package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.FeeConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.FeeConfigurationRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
@Order(10)
public class FeeMultiplierStrategy implements PremiumMultiplierStrategy {

    private final FeeConfigurationRepository feeRepo;

    public FeeMultiplierStrategy(FeeConfigurationRepository feeRepo) {
        this.feeRepo = feeRepo;
    }

    @Override
    public BigDecimal multiplierAdjustment(PolicyCalculationContext ctx) {
        return feeRepo.findAll().stream()
                .filter(FeeConfigurationEntity::isActive)
                .filter(f -> isEffectiveOn(f, ctx.effectiveDate()))
                .map(FeeConfigurationEntity::getPercentage)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isEffectiveOn(FeeConfigurationEntity f, java.time.LocalDate date) {
        if (f.getEffectiveFrom() != null && f.getEffectiveFrom().isAfter(date)) return false;
        return f.getEffectiveTo() == null || !f.getEffectiveTo().isBefore(date);
    }
}