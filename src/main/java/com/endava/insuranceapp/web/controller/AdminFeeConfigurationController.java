package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.FeeConfigurationAdminService;
import com.endava.insuranceapp.web.dto.FeeConfigurationCreateRequest;
import com.endava.insuranceapp.web.dto.FeeConfigurationResponse;
import com.endava.insuranceapp.web.dto.FeeConfigurationUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fees")
public class AdminFeeConfigurationController {

    private final FeeConfigurationAdminService service;

    public AdminFeeConfigurationController(FeeConfigurationAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<FeeConfigurationResponse> list() {
        return service.listAll();
    }

    @PostMapping
    public ResponseEntity<FeeConfigurationResponse> create(@Valid @RequestBody FeeConfigurationCreateRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public FeeConfigurationResponse update(@PathVariable Long id, @Valid @RequestBody FeeConfigurationUpdateRequest request) {
        return service.update(id, request);
    }
}
