package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.FeeConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.FeeConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FeeMultiplierStrategyTest {

    private FeeConfigurationRepository feeRepo;
    private FeeMultiplierStrategy strategy;

    @BeforeEach
    void setUp() {
        feeRepo = mock(FeeConfigurationRepository.class);
        strategy = new FeeMultiplierStrategy(feeRepo);
    }

    @Test
    void multiplierAdjustment_sumsOnlyActiveAndEffectiveFees() {
        LocalDate today = LocalDate.now();

        FeeConfigurationEntity activeEffective = fee(true, today.minusDays(10), null, new BigDecimal("0.02"));
        FeeConfigurationEntity activeNotYet = fee(true, today.plusDays(1), null, new BigDecimal("0.50"));
        FeeConfigurationEntity inactive = fee(false, today.minusDays(10), null, new BigDecimal("0.30"));
        FeeConfigurationEntity activeExpired = fee(true, today.minusDays(10), today.minusDays(1), new BigDecimal("0.40"));
        FeeConfigurationEntity activeEffective2 = fee(true, today.minusDays(1), today.plusDays(1), new BigDecimal("0.01"));

        when(feeRepo.findAll()).thenReturn(List.of(activeEffective, activeNotYet, inactive, activeExpired, activeEffective2));

        PolicyCalculationContext ctx = new PolicyCalculationContext(today, null, null);

        BigDecimal res = strategy.multiplierAdjustment(ctx);

        // only 0.02 + 0.01 should apply
        assertEquals(new BigDecimal("0.03"), res);
    }

    private FeeConfigurationEntity fee(boolean active, LocalDate from, LocalDate to, BigDecimal pct) {
        FeeConfigurationEntity f = new FeeConfigurationEntity();
        f.setActive(active);
        f.setEffectiveFrom(from);
        f.setEffectiveTo(to);
        f.setPercentage(pct);
        return f;
    }
}