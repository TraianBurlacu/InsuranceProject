package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.RiskFactorConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.RiskFactorConfigurationRepository;
import com.endava.insuranceapp.web.dto.*;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class RiskFactorAdminService {

    private static final Logger log = LoggerFactory.getLogger(RiskFactorAdminService.class);

    private final RiskFactorConfigurationRepository repo;

    public RiskFactorAdminService(RiskFactorConfigurationRepository repo) {
        this.repo = repo;
    }

    public List<RiskFactorResponse> listAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public RiskFactorResponse create(RiskFactorCreateRequest request) {
        validate(request.level(), request.referenceId(), request.buildingType());

        RiskFactorConfigurationEntity e = new RiskFactorConfigurationEntity();
        e.setLevel(request.level().trim());
        e.setReferenceId(request.referenceId());
        e.setBuildingType(request.buildingType());
        e.setAdjustmentPercentage(request.adjustmentPercentage());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        log.info("Created risk factor configuration level={}, referenceId={}, buildingType={}",
                e.getLevel(), e.getReferenceId(), e.getBuildingType());
        return toResponse(repo.save(e));
    }

    public RiskFactorResponse update(Long id, RiskFactorUpdateRequest request) {
        validate(request.level(), request.referenceId(), request.buildingType());

        RiskFactorConfigurationEntity e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskFactorConfiguration with id " + id + " not found"));

        e.setLevel(request.level().trim());
        e.setReferenceId(request.referenceId());
        e.setBuildingType(request.buildingType());
        e.setAdjustmentPercentage(request.adjustmentPercentage());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        log.info("Updated risk factor configuration id={}, level={}, referenceId={}, buildingType={}",
                e.getId(), e.getLevel(), e.getReferenceId(), e.getBuildingType());
        return toResponse(repo.save(e));
    }

    private void validate(String level, Long referenceId, String buildingType) {
        String lvl = level == null ? "" : level.trim();
        boolean buildingTypeLevel = "BuildingType".equalsIgnoreCase(lvl);

        if (buildingTypeLevel) {
            if (buildingType == null || buildingType.isBlank()) {
                log.warn("Building type is null or blank");
                throw new IllegalArgumentException("buildingType is required when level is BuildingType");
            }
        } else {
            if (referenceId == null) {
                log.warn("Reference id is null");
                throw new IllegalArgumentException("referenceId is required when level is not BuildingType");
            }
        }

        log.info("Validated risk factor configuration level={}, referenceId={}, buildingType={}", level, referenceId, buildingType);
    }

    private RiskFactorResponse toResponse(RiskFactorConfigurationEntity e) {
        return new RiskFactorResponse(
                e.getId(),
                e.getLevel(),
                e.getReferenceId(),
                e.getBuildingType(),
                e.getAdjustmentPercentage(),
                e.isActive()
        );
    }
}
