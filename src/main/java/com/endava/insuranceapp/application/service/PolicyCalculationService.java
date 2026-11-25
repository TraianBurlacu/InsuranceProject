package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.application.service.calculation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.BuildingEntity;

@Service
public class PolicyCalculationService {

    private static final Logger log = LoggerFactory.getLogger(PolicyCalculationService.class);

    private final List<PremiumMultiplierStrategy> multiplierStrategies;
    private final List<PremiumSurchargeStrategy> surchargeStrategies;

    public PolicyCalculationService(List<PremiumMultiplierStrategy> multiplierStrategies,
                                    List<PremiumSurchargeStrategy> surchargeStrategies) {
        this.multiplierStrategies = multiplierStrategies;
        this.surchargeStrategies = surchargeStrategies;
    }

    public BigDecimal computeFinalPremium(BigDecimal base,
                                          LocalDate effectiveDate,
                                          BuildingEntity building,
                                          BrokerEntity broker) {

        Objects.requireNonNull(base, "base is required");
        Objects.requireNonNull(effectiveDate, "effectiveDate is required");
        Objects.requireNonNull(building, "building is required");
        Objects.requireNonNull(broker, "broker is required");

        PolicyCalculationContext ctx = new PolicyCalculationContext(effectiveDate, building, broker);

        BigDecimal sumAdjustments = multiplierStrategies.stream()
                .map(s -> safe(s.multiplierAdjustment(ctx)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = base.multiply(BigDecimal.ONE.add(sumAdjustments));

        BigDecimal totalSurcharges = surchargeStrategies.stream()
                .map(s -> safe(s.surcharge(ctx, subtotal)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalPremium = subtotal.add(totalSurcharges);

        log.info("Premium calculation: base={}, multiplierAdj={}, subtotal={}, surcharges={}, final={}",
                base, sumAdjustments, subtotal, totalSurcharges, finalPremium);

        return finalPremium;
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}