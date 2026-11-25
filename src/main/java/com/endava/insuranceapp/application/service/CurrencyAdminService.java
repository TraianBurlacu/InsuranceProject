package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.CurrencyRepository;
import com.endava.insuranceapp.web.dto.CurrencyCreateRequest;
import com.endava.insuranceapp.web.dto.CurrencyResponse;
import com.endava.insuranceapp.web.dto.CurrencyUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CurrencyAdminService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyAdminService.class);

    private final CurrencyRepository currencyRepository;

    public CurrencyAdminService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<CurrencyResponse> listAll() {
        return currencyRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CurrencyResponse create(CurrencyCreateRequest request) {
        String code = request.code().trim().toUpperCase();

        if (currencyRepository.existsByCodeIgnoreCase(code)) {
            log.warn("Currency with code {} already exists", code);
            throw new IllegalArgumentException("currency code already exists");
        }

        CurrencyEntity e = new CurrencyEntity();
        e.setCode(code);
        e.setName(request.name().trim());
        e.setExchangeRateToBase(request.exchangeRateToBase());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        log.info("Created currency with code {}", e.getCode());
        return toResponse(currencyRepository.save(e));
    }

    public CurrencyResponse update(Long id, CurrencyUpdateRequest request) {
        CurrencyEntity e = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency with id " + id + " not found"));

        e.setName(request.name().trim());
        e.setExchangeRateToBase(request.exchangeRateToBase());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        log.info("Updated currency with code {}", e.getCode());
        return toResponse(currencyRepository.save(e));
    }

    private CurrencyResponse toResponse(CurrencyEntity e) {
        return new CurrencyResponse(
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getExchangeRateToBase(),
                e.isActive()
        );
    }
}
