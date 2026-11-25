package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.CurrencyRepository;
import com.endava.insuranceapp.application.exception.ExchangeRateUnavailableException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converts amounts using the exchangeRateToBase stored in CurrencyEntity.
 *
 * Convention: currency.exchangeRateToBase = RON per 1 unit of currency.
 *
 * Convert formula:
 * - amount_from * rate(from) / rate(to)
 *
 * Example: convert 100 USD -> EUR:
 *   rate(USD) = 4.60 (RON per 1 USD)
 *   rate(EUR) = 4.97 (RON per 1 EUR)
 *   result = 100 * 4.60 / 4.97
 */
@Service
public class CurrencyConversionService {

    private final CurrencyRepository currencyRepository;

    // final money scale
    private static final int MONEY_SCALE = 2;
    // internal calculation scale
    private static final int INTERNAL_SCALE = 10;

    public CurrencyConversionService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public BigDecimal convert(BigDecimal amount, String fromCode, String toCode) {
        if (amount == null) throw new IllegalArgumentException("amount is required");
        if (fromCode == null || toCode == null) throw new IllegalArgumentException("currency codes are required");

        String from = fromCode.toUpperCase();
        String to = toCode.toUpperCase();

        if (from.equals(to)) {
            return amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal fromRate = getRateToRon(from);
        BigDecimal toRate = getRateToRon(to);

        // amount_to = amount_from * fromRate / toRate
        BigDecimal result = amount.multiply(fromRate)
                .divide(toRate, INTERNAL_SCALE, RoundingMode.HALF_UP);

        return result.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getRateToRon(String code) {
        if ("RON".equalsIgnoreCase(code)) {
            return BigDecimal.ONE;
        }

        CurrencyEntity entity = currencyRepository.findByCodeIgnoreCaseAndActiveTrue(code)
                .orElseThrow(() -> new ExchangeRateUnavailableException("Active currency not found: " + code));

        BigDecimal rate = entity.getExchangeRateToBase();
        if (rate == null || rate.signum() <= 0) {
            throw new ExchangeRateUnavailableException("Invalid exchange rate for " + code);
        }

        return rate;
    }
}