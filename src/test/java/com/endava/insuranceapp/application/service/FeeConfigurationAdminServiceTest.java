package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.FeeConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.FeeConfigurationRepository;
import com.endava.insuranceapp.web.dto.FeeConfigurationCreateRequest;
import com.endava.insuranceapp.web.dto.FeeConfigurationResponse;
import com.endava.insuranceapp.web.dto.FeeConfigurationUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeConfigurationAdminServiceTest {

    private FeeConfigurationRepository repo;
    private FeeConfigurationAdminService service;

    @BeforeEach
    void setUp() {
        repo = mock(FeeConfigurationRepository.class);
        service = new FeeConfigurationAdminService(repo);
    }

    @Test
    void listAll_mapsEntitiesToResponses() {
        FeeConfigurationEntity e1 = new FeeConfigurationEntity();
        e1.setName("Issuance");
        e1.setType("PERCENT");
        e1.setPercentage(new BigDecimal("0.05"));
        e1.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        e1.setEffectiveTo(null);
        e1.setActive(true);

        when(repo.findAll()).thenReturn(List.of(e1));

        List<FeeConfigurationResponse> out = service.listAll();

        assertEquals(1, out.size());
        assertEquals("Issuance", out.get(0).name());
        assertEquals("PERCENT", out.get(0).type());
        assertEquals(new BigDecimal("0.05"), out.get(0).percentage());
        assertEquals(LocalDate.of(2026, 1, 1), out.get(0).effectiveFrom());
        assertNull(out.get(0).effectiveTo());
        assertTrue(out.get(0).isActive());
    }

    @Test
    void create_success_trimsFields_mapsAndSaves() {
        FeeConfigurationCreateRequest req = new FeeConfigurationCreateRequest(
                "  Issuance  ",
                "  PERCENT  ",
                new BigDecimal("0.05"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                true
        );

        ArgumentCaptor<FeeConfigurationEntity> captor = ArgumentCaptor.forClass(FeeConfigurationEntity.class);
        when(repo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        FeeConfigurationResponse resp = service.create(req);

        FeeConfigurationEntity saved = captor.getValue();
        assertEquals("Issuance", saved.getName());
        assertEquals("PERCENT", saved.getType());
        assertEquals(new BigDecimal("0.05"), saved.getPercentage());
        assertEquals(LocalDate.of(2026, 1, 1), saved.getEffectiveFrom());
        assertEquals(LocalDate.of(2026, 12, 31), saved.getEffectiveTo());
        assertTrue(saved.isActive());

        assertEquals("Issuance", resp.name());
        assertEquals("PERCENT", resp.type());
        assertTrue(resp.isActive());
    }

    @Test
    void create_invalidPeriod_throws400() {
        FeeConfigurationCreateRequest req = new FeeConfigurationCreateRequest(
                "Issuance",
                "PERCENT",
                new BigDecimal("0.05"),
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1), // to < from
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertTrue(ex.getMessage().contains("effectiveTo must be"));
        verify(repo, never()).save(any());
    }

    @Test
    void update_notFound_throws404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        FeeConfigurationUpdateRequest req = new FeeConfigurationUpdateRequest(
                "Issuance",
                "PERCENT",
                new BigDecimal("0.10"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                false
        );

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, req));
        verify(repo, never()).save(any());
    }

    @Test
    void update_success_updatesEntity_mapsAndSaves() {
        FeeConfigurationEntity existing = new FeeConfigurationEntity();
        existing.setName("Issuance");
        existing.setType("PERCENT");
        existing.setPercentage(new BigDecimal("0.05"));
        existing.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        existing.setEffectiveTo(LocalDate.of(2026, 12, 31));
        existing.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FeeConfigurationUpdateRequest req = new FeeConfigurationUpdateRequest(
                "  Issuance Updated  ",
                "  PERCENT  ",
                new BigDecimal("0.10"),
                LocalDate.of(2026, 3, 1),
                null,
                false
        );

        FeeConfigurationResponse resp = service.update(1L, req);

        assertEquals("Issuance Updated", existing.getName());
        assertEquals("PERCENT", existing.getType());
        assertEquals(new BigDecimal("0.10"), existing.getPercentage());
        assertEquals(LocalDate.of(2026, 3, 1), existing.getEffectiveFrom());
        assertNull(existing.getEffectiveTo());
        assertFalse(existing.isActive());

        assertEquals("Issuance Updated", resp.name());
        assertFalse(resp.isActive());
    }

    @Test
    void update_invalidPeriod_throws400() {
        FeeConfigurationEntity existing = new FeeConfigurationEntity();
        existing.setName("Issuance");
        existing.setType("PERCENT");
        existing.setPercentage(new BigDecimal("0.05"));
        existing.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        existing.setEffectiveTo(LocalDate.of(2026, 12, 31));
        existing.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        FeeConfigurationUpdateRequest req = new FeeConfigurationUpdateRequest(
                "Issuance",
                "PERCENT",
                new BigDecimal("0.10"),
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1), // to < from
                true
        );

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, req));
        verify(repo, never()).save(any());
    }
}
