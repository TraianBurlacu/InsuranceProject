package com.endava.insuranceapp.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateResponse {

    @JsonProperty("base")
    private String base;

    @JsonProperty("date")
    private String date;

    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Map<String, BigDecimal> getRates() { return rates; }
    public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
}