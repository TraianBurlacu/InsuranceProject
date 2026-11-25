package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.client.ExchangeRateClient;
import com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExchangeRateRefreshService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateRefreshService.class);

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateClient exchangeRateClient;

    public ExchangeRateRefreshService(CurrencyRepository currencyRepository,
                                      ExchangeRateClient exchangeRateClient) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateClient = exchangeRateClient;
    }

    @Transactional
    public void refreshActiveCurrencies() {
        List<CurrencyEntity> activeCurrencies = currencyRepository.findAllByActiveTrue();

        for (CurrencyEntity c : activeCurrencies) {
            String code = c.getCode();
            if (code == null || code.isBlank()) continue;

            if ("RON".equalsIgnoreCase(code)) {
                c.setExchangeRateToBase(BigDecimal.ONE);
                continue;
            }

            try {
                BigDecimal ronPerUnit = exchangeRateClient.fetchRonPerUnit(code);
                c.setExchangeRateToBase(ronPerUnit);
                log.info("FX updated: {} -> RON rate={}", code.toUpperCase(), ronPerUnit);
            } catch (Exception e) {
                log.warn("FX update failed for {}. Keeping previous rate.", code, e);            }
        }
    }
}