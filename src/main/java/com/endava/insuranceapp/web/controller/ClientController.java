package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.ClientService;
import com.endava.insuranceapp.web.dto.ClientCreateRequest;
import com.endava.insuranceapp.web.dto.ClientResponse;
import com.endava.insuranceapp.web.dto.ClientUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokers/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientCreateRequest request) {
        return ResponseEntity.status(201).body(clientService.create(request));
    }

    @PutMapping("/{clientId}")
    public ClientResponse update(@PathVariable Long clientId, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.update(clientId, request);
    }

    @GetMapping("/{clientId}")
    public ClientResponse getById(@PathVariable Long clientId) {
        return clientService.getById(clientId);
    }

    @GetMapping
    public Page<ClientResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String identifier,
            Pageable pageable
    ) {
        return clientService.search(name, identifier, pageable);
    }
}
