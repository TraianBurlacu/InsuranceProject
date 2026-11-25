package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.RiskFactorAdminService;
import com.endava.insuranceapp.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/risk-factors")
public class AdminRiskFactorController {

    private final RiskFactorAdminService service;

    public AdminRiskFactorController(RiskFactorAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<RiskFactorResponse> list() {
        return service.listAll();
    }

    @PostMapping
    public ResponseEntity<RiskFactorResponse> create(@Valid @RequestBody RiskFactorCreateRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public RiskFactorResponse update(@PathVariable Long id, @Valid @RequestBody RiskFactorUpdateRequest request) {
        return service.update(id, request);
    }
}
