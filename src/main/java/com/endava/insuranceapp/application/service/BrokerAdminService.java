package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.BrokerRepository;
import com.endava.insuranceapp.web.dto.BrokerCreateRequest;
import com.endava.insuranceapp.web.dto.BrokerResponse;
import com.endava.insuranceapp.web.dto.BrokerUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BrokerAdminService {

    private static final Logger log = LoggerFactory.getLogger(BrokerAdminService.class);

    private final BrokerRepository repo;

    public BrokerAdminService(BrokerRepository repo) {
        this.repo = repo;
    }

    public List<BrokerResponse> listAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public BrokerResponse create(BrokerCreateRequest req) {
        String code = req.brokerCode().trim().toUpperCase();

        if (repo.existsByBrokerCodeIgnoreCase(code)) {
            log.warn("Broker with code {} already exists", code);
            throw new IllegalArgumentException("brokerCode already exists");
        }

        BrokerEntity e = new BrokerEntity();
        e.setBrokerCode(code);
        e.setName(req.name().trim());
        e.setEmail(req.email() == null ? null : req.email().trim());
        e.setPhone(req.phone());
        e.setActive(Boolean.TRUE.equals(req.isActive()));
        e.setCommissionPercentage(req.commissionPercentage());
        e.setCreatedAt(OffsetDateTime.now());

        log.info("Created broker name={}, brokerCode={}", e.getName(), e.getBrokerCode());
        return toResponse(repo.save(e));
    }

    public BrokerResponse update(Long id, BrokerUpdateRequest req) {
        BrokerEntity e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Broker with id " + id + " not found"));

        e.setName(req.name().trim());
        e.setEmail(req.email() == null ? null : req.email().trim());
        e.setPhone(req.phone());
        e.setActive(Boolean.TRUE.equals(req.isActive()));
        e.setCommissionPercentage(req.commissionPercentage());
        e.setUpdatedAt(OffsetDateTime.now());

        log.info("Updated broker name={}, brokerCode={}", e.getName(), e.getBrokerCode());
        return toResponse(repo.save(e));
    }

    public BrokerResponse activate(Long id) {
        BrokerEntity e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Broker with id " + id + " not found"));
        e.setActive(true);
        e.setUpdatedAt(OffsetDateTime.now());

        log.info("Activated broker name={}, brokerCode={}", e.getName(), e.getBrokerCode());
        return toResponse(repo.save(e));
    }

    public BrokerResponse deactivate(Long id) {
        BrokerEntity e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Broker with id " + id + " not found"));
        e.setActive(false);
        e.setUpdatedAt(OffsetDateTime.now());

        log.info("Deactivated broker name={}, brokerCode={}", e.getName(), e.getBrokerCode());
        return toResponse(repo.save(e));
    }

    public BrokerResponse getById(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Broker with id " + id + " not found"));
    }

    private BrokerResponse toResponse(BrokerEntity e) {
        return new BrokerResponse(
                e.getId(),
                e.getBrokerCode(),
                e.getName(),
                e.getEmail(),
                e.getPhone(),
                e.isActive(),
                e.getCommissionPercentage(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
