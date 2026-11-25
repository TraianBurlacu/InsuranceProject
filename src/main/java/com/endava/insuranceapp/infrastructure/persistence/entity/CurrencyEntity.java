package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currencies")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code; // RON, USD, EUR

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "exchange_rate_to_base", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRateToBase;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getExchangeRateToBase() { return exchangeRateToBase; }
    public void setExchangeRateToBase(BigDecimal exchangeRateToBase) { this.exchangeRateToBase = exchangeRateToBase; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
