package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.CurrencyRepository;
import com.endava.insuranceapp.web.dto.CurrencyCreateRequest;
import com.endava.insuranceapp.web.dto.CurrencyUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyAdminServiceTest {

    private CurrencyRepository currencyRepository;
    private CurrencyAdminService currencyAdminService;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyAdminService = new CurrencyAdminService(currencyRepository);
    }

    @Test
    void create_whenDuplicateCode_throwsIllegalArgumentException() {
        when(currencyRepository.existsByCodeIgnoreCase("USD")).thenReturn(true);

        var req = new CurrencyCreateRequest("USD", "US Dollar", new BigDecimal("4.100000"), true);

        assertThrows(IllegalArgumentException.class, () -> currencyAdminService.create(req));
        verify(currencyRepository, never()).save(any(CurrencyEntity.class));
    }

    @Test
    void update_whenMissing_throwsNotFound() {
        when(currencyRepository.findById(99L)).thenReturn(Optional.empty());

        var req = new CurrencyUpdateRequest("Euro", new BigDecimal("4.950000"), true);

        assertThrows(ResourceNotFoundException.class, () -> currencyAdminService.update(99L, req));
    }
}
