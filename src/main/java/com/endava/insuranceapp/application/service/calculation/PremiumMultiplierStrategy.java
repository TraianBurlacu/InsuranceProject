package com.endava.insuranceapp.application.service.calculation;

import java.math.BigDecimal;

public interface PremiumMultiplierStrategy {

    BigDecimal multiplierAdjustment(PolicyCalculationContext ctx);
}