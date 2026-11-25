package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.BrokerAdminService;
import com.endava.insuranceapp.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brokers")
public class AdminBrokerController {

    private final BrokerAdminService service;

    public AdminBrokerController(BrokerAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<BrokerResponse> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public BrokerResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<BrokerResponse> create(@Valid @RequestBody BrokerCreateRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public BrokerResponse update(@PathVariable Long id, @Valid @RequestBody BrokerUpdateRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/activate")
    public BrokerResponse activate(@PathVariable Long id) {
        return service.activate(id);
    }

    @PostMapping("/{id}/deactivate")
    public BrokerResponse deactivate(@PathVariable Long id) {
        return service.deactivate(id);
    }
}
