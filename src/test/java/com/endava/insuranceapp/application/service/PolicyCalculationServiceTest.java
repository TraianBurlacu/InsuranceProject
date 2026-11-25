package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.application.service.calculation.PolicyCalculationContext;
import com.endava.insuranceapp.application.service.calculation.PremiumMultiplierStrategy;
import com.endava.insuranceapp.application.service.calculation.PremiumSurchargeStrategy;
import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.BuildingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyCalculationServiceTest {

    private PremiumMultiplierStrategy fees;
    private PremiumMultiplierStrategy risks;
    private PremiumSurchargeStrategy commission;

    private PolicyCalculationService calc;

    @BeforeEach
    void setUp() {
        fees = mock(PremiumMultiplierStrategy.class);
        risks = mock(PremiumMultiplierStrategy.class);
        commission = mock(PremiumSurchargeStrategy.class);

        calc = new PolicyCalculationService(
                List.of(fees, risks),
                List.of(commission)
        );
    }

    @Test
    void computeFinalPremium_noAdjustments_returnsBase() {
        when(fees.multiplierAdjustment(any())).thenReturn(BigDecimal.ZERO);
        when(risks.multiplierAdjustment(any())).thenReturn(BigDecimal.ZERO);
        when(commission.surcharge(any(), any())).thenReturn(BigDecimal.ZERO);

        BuildingEntity building = new BuildingEntity();
        BrokerEntity broker = new BrokerEntity();

        BigDecimal base = new BigDecimal("1000");
        BigDecimal res = calc.computeFinalPremium(base, LocalDate.now(), building, broker);

        // subtotal = 1000 * (1 + 0 + 0) = 1000
        // surcharge = 0
        assertEquals(new BigDecimal("1000.000000"), res.setScale(6));
    }

    @Test
    void computeFinalPremium_withMultipliersAndCommission_appliesCorrectOrder() {
        // multiplier adjustments: fees 0.03 + risks 0.03 = 0.06
        when(fees.multiplierAdjustment(any())).thenReturn(new BigDecimal("0.03"));
        when(risks.multiplierAdjustment(any())).thenReturn(new BigDecimal("0.03"));

        // subtotal = 1000 * 1.06 = 1060
        // commission surcharge applied on subtotal, for example 10% => 106
        when(commission.surcharge(any(), eq(new BigDecimal("1060.00"))))
                .thenReturn(new BigDecimal("106.00"));

        BuildingEntity building = new BuildingEntity();
        BrokerEntity broker = new BrokerEntity();

        BigDecimal base = new BigDecimal("1000");
        BigDecimal res = calc.computeFinalPremium(base, LocalDate.now(), building, broker);

        // final = subtotal + commission = 1060 + 106 = 1166
        assertEquals(new BigDecimal("1166.000000"), res.setScale(6));
    }

    @Test
    void computeFinalPremium_callsSurchargeWithSubtotal() {
        when(fees.multiplierAdjustment(any())).thenReturn(new BigDecimal("0.10"));
        when(risks.multiplierAdjustment(any())).thenReturn(BigDecimal.ZERO);

        BuildingEntity building = new BuildingEntity();
        BrokerEntity broker = new BrokerEntity();

        // subtotal should be 1000 * 1.10 = 1100
        when(commission.surcharge(any(), any())).thenReturn(BigDecimal.ZERO);

        calc.computeFinalPremium(new BigDecimal("1000"), LocalDate.now(), building, broker);

        // verify surcharge strategy receives subtotal, not base
        verify(commission, times(1)).surcharge(any(PolicyCalculationContext.class), eq(new BigDecimal("1100.00")));
    }
}