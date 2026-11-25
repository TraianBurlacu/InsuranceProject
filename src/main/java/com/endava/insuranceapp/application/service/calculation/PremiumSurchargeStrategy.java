package com.endava.insuranceapp.application.service.calculation;

import java.math.BigDecimal;

public interface PremiumSurchargeStrategy {

    BigDecimal surcharge(PolicyCalculationContext ctx, BigDecimal subtotal);
}