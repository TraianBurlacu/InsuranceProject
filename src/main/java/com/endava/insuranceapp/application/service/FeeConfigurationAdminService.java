package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.FeeConfigurationEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.FeeConfigurationRepository;
import com.endava.insuranceapp.web.dto.FeeConfigurationCreateRequest;
import com.endava.insuranceapp.web.dto.FeeConfigurationResponse;
import com.endava.insuranceapp.web.dto.FeeConfigurationUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class FeeConfigurationAdminService {

    private static final Logger log = LoggerFactory.getLogger(FeeConfigurationAdminService.class);

    private final FeeConfigurationRepository repo;

    public FeeConfigurationAdminService(FeeConfigurationRepository repo) {
        this.repo = repo;
    }

    public List<FeeConfigurationResponse> listAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public FeeConfigurationResponse create(FeeConfigurationCreateRequest request) {
        validatePeriod(request.effectiveFrom(), request.effectiveTo());

        FeeConfigurationEntity e = new FeeConfigurationEntity();
        e.setName(request.name().trim());
        e.setType(request.type().trim());
        e.setPercentage(request.percentage());
        e.setEffectiveFrom(request.effectiveFrom());
        e.setEffectiveTo(request.effectiveTo());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        return toResponse(repo.save(e));
    }

    public FeeConfigurationResponse update(Long id, FeeConfigurationUpdateRequest request) {
        validatePeriod(request.effectiveFrom(), request.effectiveTo());

        FeeConfigurationEntity e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeConfiguration with id " + id + " not found"));

        e.setName(request.name().trim());
        e.setType(request.type().trim());
        e.setPercentage(request.percentage());
        e.setEffectiveFrom(request.effectiveFrom());
        e.setEffectiveTo(request.effectiveTo());
        e.setActive(Boolean.TRUE.equals(request.isActive()));

        log.info("Updated FeeConfiguration with id {}", e.getId());
        return toResponse(repo.save(e));
    }

    private void validatePeriod(java.time.LocalDate from, java.time.LocalDate to) {
        if (to != null && to.isBefore(from)) {
            log.warn("Invalid period: from {} to {}", from, to);
            throw new IllegalArgumentException("effectiveTo must be >= effectiveFrom");
        }
    }

    private FeeConfigurationResponse toResponse(FeeConfigurationEntity e) {
        return new FeeConfigurationResponse(
                e.getId(),
                e.getName(),
                e.getType(),
                e.getPercentage(),
                e.getEffectiveFrom(),
                e.getEffectiveTo(),
                e.isActive()
        );
    }
}
