package com.endava.insuranceapp.infrastructure.client;

import com.endava.insuranceapp.application.exception.ExchangeRateUnavailableException;
import com.endava.insuranceapp.web.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class ExchangeRateClient {

    private final RestClient restClient;

    public ExchangeRateClient(RestClient exchangeRateRestClient) {
        this.restClient = exchangeRateRestClient;
    }

    /**
     * Returns how many RON for 1 unit of currencyCode.
     * Example: EUR -> 4.97 means 1 EUR = 4.97 RON
     */
    public BigDecimal fetchRonPerUnit(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("currencyCode is required");
        }

        String code = currencyCode.toUpperCase();

        if ("RON".equals(code)) {
            return BigDecimal.ONE;
        }

        try {
            ExchangeRateResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/latest")
                            .queryParam("base", code)
                            .queryParam("symbols", "RON")
                            .build())
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response == null || response.getRates() == null) {
                throw new ExchangeRateUnavailableException("Invalid FX provider response for " + code);
            }

            BigDecimal ron = response.getRates().get("RON");
            if (ron == null || ron.signum() <= 0) {
                throw new ExchangeRateUnavailableException("Missing/invalid RON rate for " + code);
            }

            return ron;
        } catch (Exception e) {
            throw new ExchangeRateUnavailableException("Failed to fetch FX for " + code, e);
        }
    }
}