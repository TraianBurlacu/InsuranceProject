package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.BuildingEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BrokerCommissionSurchargeStrategyTest {

    private final BrokerCommissionSurchargeStrategy strategy = new BrokerCommissionSurchargeStrategy();

    @Test
    void surcharge_nullCommission_returnsZero() {
        BrokerEntity broker = new BrokerEntity();
        broker.setCommissionPercentage(null);

        PolicyCalculationContext ctx = new PolicyCalculationContext(LocalDate.now(), new BuildingEntity(), broker);
        BigDecimal res = strategy.surcharge(ctx, new BigDecimal("1000"));

        assertEquals(BigDecimal.ZERO, res);
    }

    @Test
    void surcharge_appliesCommissionOnSubtotal() {
        BrokerEntity broker = new BrokerEntity();
        broker.setCommissionPercentage(new BigDecimal("0.10"));

        PolicyCalculationContext ctx = new PolicyCalculationContext(LocalDate.now(), new BuildingEntity(), broker);

        BigDecimal res = strategy.surcharge(ctx, new BigDecimal("1060"));

        // 10% of 1060 = 106
        assertEquals(new BigDecimal("106.0"), res.setScale(1));
    }

    @Test
    void surcharge_negativeCommission_throws() {
        BrokerEntity broker = new BrokerEntity();
        broker.setCommissionPercentage(new BigDecimal("-0.01"));

        PolicyCalculationContext ctx = new PolicyCalculationContext(LocalDate.now(), new BuildingEntity(), broker);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.surcharge(ctx, new BigDecimal("1000")));

        assertTrue(ex.getMessage().toLowerCase().contains("commission"));
    }
}