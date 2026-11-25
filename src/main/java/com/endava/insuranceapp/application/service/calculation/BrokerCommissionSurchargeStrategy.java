package com.endava.insuranceapp.application.service.calculation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(10)
public class BrokerCommissionSurchargeStrategy implements PremiumSurchargeStrategy {

    @Override
    public BigDecimal surcharge(PolicyCalculationContext ctx, BigDecimal subtotal) {
        BigDecimal commission = ctx.broker() != null ? ctx.broker().getCommissionPercentage() : null;
        if (commission == null) return BigDecimal.ZERO;

        if (commission.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("commissionPercentage cannot be negative");
        }

        return subtotal.multiply(commission);
    }
}