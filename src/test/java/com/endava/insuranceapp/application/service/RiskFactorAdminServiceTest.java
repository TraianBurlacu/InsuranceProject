package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.RiskFactorConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.RiskFactorConfigurationRepository;
import com.endava.insuranceapp.web.dto.RiskFactorCreateRequest;
import com.endava.insuranceapp.web.dto.RiskFactorResponse;
import com.endava.insuranceapp.web.dto.RiskFactorUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskFactorAdminServiceTest {

    private RiskFactorConfigurationRepository repo;
    private RiskFactorAdminService service;

    @BeforeEach
    void setUp() {
        repo = mock(RiskFactorConfigurationRepository.class);
        service = new RiskFactorAdminService(repo);
    }

    @Test
    void listAll_mapsEntitiesToResponses() {
        RiskFactorConfigurationEntity e1 = new RiskFactorConfigurationEntity();
        e1.setLevel("City");
        e1.setReferenceId(1L);
        e1.setBuildingType(null);
        e1.setAdjustmentPercentage(new BigDecimal("0.02"));
        e1.setActive(true);

        RiskFactorConfigurationEntity e2 = new RiskFactorConfigurationEntity();
        e2.setLevel("BuildingType");
        e2.setReferenceId(null);
        e2.setBuildingType("INDUSTRIAL");
        e2.setAdjustmentPercentage(new BigDecimal("0.05"));
        e2.setActive(false);

        when(repo.findAll()).thenReturn(List.of(e1, e2));

        List<RiskFactorResponse> list = service.listAll();

        assertEquals(2, list.size());
        assertEquals("City", list.get(0).level());
        assertEquals(1L, list.get(0).referenceId());
        assertNull(list.get(0).buildingType());

        assertEquals("BuildingType", list.get(1).level());
        assertNull(list.get(1).referenceId());
        assertEquals("INDUSTRIAL", list.get(1).buildingType());
    }

    @Test
    void create_whenLevelBuildingType_andBuildingTypeMissing_throws400() {
        RiskFactorCreateRequest req = new RiskFactorCreateRequest(
                "BuildingType",
                null,
                "   ", // blank
                new BigDecimal("0.05"),
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertTrue(ex.getMessage().toLowerCase().contains("buildingtype is required"));
        verify(repo, never()).save(any());
    }

    @Test
    void create_whenLevelNotBuildingType_andReferenceIdMissing_throws400() {
        RiskFactorCreateRequest req = new RiskFactorCreateRequest(
                "City",
                null, // missing
                null,
                new BigDecimal("0.03"),
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertTrue(ex.getMessage().toLowerCase().contains("referenceid is required"));
        verify(repo, never()).save(any());
    }

    @Test
    void create_success_trimsLevel_mapsFields_andSaves() {
        RiskFactorCreateRequest req = new RiskFactorCreateRequest(
                "  City  ", // should be trimmed
                1L,
                null,
                new BigDecimal("0.02"),
                true
        );

        ArgumentCaptor<RiskFactorConfigurationEntity> captor = ArgumentCaptor.forClass(RiskFactorConfigurationEntity.class);
        when(repo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        RiskFactorResponse resp = service.create(req);

        RiskFactorConfigurationEntity saved = captor.getValue();
        assertEquals("City", saved.getLevel());
        assertEquals(1L, saved.getReferenceId());
        assertNull(saved.getBuildingType());
        assertEquals(new BigDecimal("0.02"), saved.getAdjustmentPercentage());
        assertTrue(saved.isActive());

        assertEquals("City", resp.level());
        assertEquals(1L, resp.referenceId());
        assertTrue(resp.isActive());
    }

    @Test
    void update_notFound_throws404() {
        when(repo.findById(7L)).thenReturn(Optional.empty());

        RiskFactorUpdateRequest req = new RiskFactorUpdateRequest(
                "City",
                1L,
                null,
                new BigDecimal("0.02"),
                true
        );

        assertThrows(ResourceNotFoundException.class, () -> service.update(7L, req));
        verify(repo, never()).save(any());
    }

    @Test
    void update_success_updatesEntity_andSaves() {
        RiskFactorConfigurationEntity existing = new RiskFactorConfigurationEntity();
        existing.setLevel("City");
        existing.setReferenceId(1L);
        existing.setBuildingType(null);
        existing.setAdjustmentPercentage(new BigDecimal("0.01"));
        existing.setActive(false);

        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RiskFactorUpdateRequest req = new RiskFactorUpdateRequest(
                "  BuildingType  ", // trim + triggers buildingType validation
                null,
                "OFFICE",
                new BigDecimal("0.06"),
                true
        );

        RiskFactorResponse resp = service.update(5L, req);

        assertEquals("BuildingType", existing.getLevel());
        assertNull(existing.getReferenceId());
        assertEquals("OFFICE", existing.getBuildingType());
        assertEquals(new BigDecimal("0.06"), existing.getAdjustmentPercentage());
        assertTrue(existing.isActive());

        assertEquals("BuildingType", resp.level());
        assertEquals("OFFICE", resp.buildingType());
        assertTrue(resp.isActive());
    }
}
