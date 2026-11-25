package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.PolicyService;
import com.endava.insuranceapp.web.dto.PolicyCreateRequest;
import com.endava.insuranceapp.web.dto.PolicyResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/brokers/policies")
public class PolicyController {

    private final PolicyService service;

    public PolicyController(PolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PolicyResponse> createDraft(@Valid @RequestBody PolicyCreateRequest request) {
        return ResponseEntity.status(201).body(service.createDraft(request));
    }

    @PostMapping("/{id}/activate")
    public PolicyResponse activate(@PathVariable Long id) {
        return service.activate(id);
    }

    @PostMapping("/{id}/cancel")
    public PolicyResponse cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return service.cancel(id, reason);
    }

    @GetMapping("/{id}")
    public PolicyResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<PolicyResponse> search(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long brokerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return service.search(clientId, brokerId, status, startDate, endDate);
    }
}
