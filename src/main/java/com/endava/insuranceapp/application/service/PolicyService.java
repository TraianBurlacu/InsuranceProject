package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.*;
import com.endava.insuranceapp.web.dto.PolicyCreateRequest;
import com.endava.insuranceapp.web.dto.PolicyResponse;
import com.endava.insuranceapp.application.service.CurrencyConversionService;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepo;
    private final ClientRepository clientRepo;
    private final BuildingRepository buildingRepo;
    private final BrokerRepository brokerRepo;
    private final CurrencyRepository currencyRepo;
    private final PolicyCalculationService calcService;
    private final CurrencyConversionService conversionService;

    public PolicyService(PolicyRepository policyRepo,
                         ClientRepository clientRepo,
                         BuildingRepository buildingRepo,
                         BrokerRepository brokerRepo,
                         CurrencyRepository currencyRepo,
                         PolicyCalculationService calcService,
                         CurrencyConversionService conversionService) {
        this.policyRepo = policyRepo;
        this.clientRepo = clientRepo;
        this.buildingRepo = buildingRepo;
        this.brokerRepo = brokerRepo;
        this.currencyRepo = currencyRepo;
        this.calcService = calcService;
        this.conversionService = conversionService;
    }

    @Transactional
    public PolicyResponse createDraft(PolicyCreateRequest req) {
        ClientEntity client = clientRepo.findById(req.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        BuildingEntity building = buildingRepo.findById(req.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found"));

        if (!building.getClient().getId().equals(client.getId())) {
            log.warn("Building id={} does not belong to client id={}", building.getId(), client.getId());
            throw new IllegalArgumentException("Building does not belong to client");
        }

        String brokerCode = req.brokerCode().trim();

        BrokerEntity broker = brokerRepo.findByBrokerCodeIgnoreCaseAndActiveTrue(brokerCode)
                .orElseGet(() -> {
                    brokerRepo.findByBrokerCodeIgnoreCase(brokerCode)
                            .orElseThrow(() -> new ResourceNotFoundException("Broker with code " + brokerCode + " not found"));
                    throw new IllegalArgumentException("Broker is inactive");
                });

        if (!broker.isActive()) {
            log.warn("Broker with code {} is not active", brokerCode);
            throw new IllegalArgumentException("Broker is inactive");
        }

        CurrencyEntity currency = currencyRepo.findByCodeIgnoreCase(req.currencyCode())
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found"));

        // IMPORTANT:
        // Request basePremium is in req.currencyCode()
        // Convert it to RON for internal calculation and storage.
        BigDecimal basePremiumRon = conversionService.convert(req.basePremium(), req.currencyCode(), "RON");

        BigDecimal finalPremiumRon = calcService.computeFinalPremium(basePremiumRon, req.startDate(), building, broker);

        PolicyEntity e = new PolicyEntity();
        e.setPolicyNumber(genPolicyNumber());
        e.setClient(client);
        e.setBuilding(building);
        e.setBroker(broker);
        e.setCurrency(currency);
        e.setStatus("DRAFT");
        e.setStartDate(req.startDate());
        e.setEndDate(req.endDate());

        // Store internal amounts in RON
        e.setBasePremium(basePremiumRon);
        e.setFinalPremium(finalPremiumRon);

        e.setCreatedAt(OffsetDateTime.now());

        PolicyEntity saved = policyRepo.save(e);

        log.info("Created policy draft id={} for client id={}", saved.getId(), client.getId());
        return toResponse(saved);
    }

    @Transactional
    public PolicyResponse activate(Long id) {
        PolicyEntity e = policyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (!"DRAFT".equalsIgnoreCase(e.getStatus())) {
            log.warn("Policy id={} is not in DRAFT status, cannot be activated", e.getId());
            throw new IllegalArgumentException("Only drafts can be activated");
        }

        validateDraftCompleteness(e);

        if (e.getStartDate().isBefore(LocalDate.now())) {
            log.warn("Policy id={} has start date = {} in the past, cannot be activated", e.getId(), e.getStartDate());
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        e.setStatus("ACTIVE");
        e.setUpdatedAt(OffsetDateTime.now());
        PolicyEntity saved = policyRepo.save(e);

        log.info("Activated policy id={} for client id={}", saved.getId(), e.getClient().getId());
        return toResponse(saved);
    }

    @Transactional
    public PolicyResponse cancel(Long id, String reason) {
        PolicyEntity e = policyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (!"ACTIVE".equalsIgnoreCase(e.getStatus())) {
            log.warn("Policy id={} is not in ACTIVE, cannot be cancelled", e.getId());
            throw new IllegalArgumentException("Only active policies can be cancelled");
        }

        e.setStatus("CANCELLED");
        e.setCancellationReason(reason);
        e.setUpdatedAt(OffsetDateTime.now());
        PolicyEntity saved = policyRepo.save(e);

        log.info("Cancelled policy id={} for client id={}", saved.getId(), e.getClient().getId());
        return toResponse(saved);
    }

    @Transactional
    public PolicyResponse getById(Long id) {
        return policyRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    }

    @Transactional
    public List<PolicyResponse> search(Long clientId, Long brokerId, String status, LocalDate startDate, LocalDate endDate) {
        return policyRepo.search(clientId, brokerId, status, startDate, endDate).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private PolicyResponse toResponse(PolicyEntity e) {
        String policyCurrency = e.getCurrency() != null ? e.getCurrency().getCode() : "RON";

        // Stored internal amounts in RON -> convert for response
        BigDecimal basePremiumOut = conversionService.convert(e.getBasePremium(), "RON", policyCurrency);
        BigDecimal finalPremiumOut = conversionService.convert(e.getFinalPremium(), "RON", policyCurrency);

        return new PolicyResponse(
                e.getId(),
                e.getPolicyNumber(),
                e.getClient().getId(),
                e.getBuilding().getId(),
                e.getBroker().getId(),
                e.getBroker().getBrokerCode(),
                policyCurrency,
                e.getStatus(),
                e.getStartDate(),
                e.getEndDate(),
                basePremiumOut,
                finalPremiumOut,
                e.getCancellationReason(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private String genPolicyNumber() {
        return "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateDraftCompleteness(PolicyEntity e) {
        if (e.getClient() == null) throw new IllegalArgumentException("client is required before activation");
        if (e.getBuilding() == null) throw new IllegalArgumentException("building is required before activation");
        if (e.getBroker() == null) throw new IllegalArgumentException("broker is required before activation");
        if (e.getCurrency() == null) throw new IllegalArgumentException("currency is required before activation");
        if (e.getStartDate() == null) throw new IllegalArgumentException("startDate is required before activation");
        if (e.getEndDate() == null) throw new IllegalArgumentException("endDate is required before activation");
        if (e.getEndDate().isBefore(e.getStartDate())) throw new IllegalArgumentException("endDate must be >= startDate");
        if (e.getBasePremium() == null || e.getBasePremium().signum() <= 0)
            throw new IllegalArgumentException("basePremium must be > 0 before activation");
        if (e.getFinalPremium() == null || e.getFinalPremium().signum() <= 0)
            throw new IllegalArgumentException("finalPremium must be > 0 before activation");
    }
}