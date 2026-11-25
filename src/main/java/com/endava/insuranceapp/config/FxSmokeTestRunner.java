package com.endava.insuranceapp.config;

import com.endava.insuranceapp.application.service.CurrencyConversionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FxSmokeTestRunner implements CommandLineRunner {

    private final CurrencyConversionService conversionService;

    public FxSmokeTestRunner(CurrencyConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public void run(String... args) {
        BigDecimal ron = conversionService.convert(new BigDecimal("100"), "EUR", "RON");
        BigDecimal eur = conversionService.convert(ron, "RON", "EUR");
        System.out.println("SMOKE FX: 100 EUR -> RON = " + ron + " ; back to EUR = " + eur);
    }
}