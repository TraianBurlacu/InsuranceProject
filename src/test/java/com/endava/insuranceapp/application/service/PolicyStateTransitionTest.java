package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.PolicyRepository;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyStateTransitionTest {

    private PolicyRepository policyRepo;
    private PolicyService policyService;
    @BeforeEach
    void setUp() {
        policyRepo = mock(PolicyRepository.class);
        policyService = new PolicyService(policyRepo, null, null, null, null, null, null);
    }

    @Test
    void activate_whenNotDraft_throws() {
        PolicyEntity p = new PolicyEntity();
        p.setStatus("ACTIVE");

        when(policyRepo.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> policyService.activate(1L));
    }

    @Test
    void cancel_whenNotActive_throws() {
        PolicyEntity p = new PolicyEntity();
        p.setStatus("DRAFT");

        when(policyRepo.findById(2L)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> policyService.cancel(2L, "reason"));
    }

    @Test
    void activate_whenMissing_throwsNotFound() {
        when(policyRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> policyService.activate(99L));
    }
}
