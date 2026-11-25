package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.BuildingService;
import com.endava.insuranceapp.web.dto.BuildingCreateRequest;
import com.endava.insuranceapp.web.dto.BuildingResponse;
import com.endava.insuranceapp.web.dto.BuildingUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brokers")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping("/clients/{clientId}/buildings")
    public ResponseEntity<BuildingResponse> create(
            @PathVariable Long clientId,
            @Valid @RequestBody BuildingCreateRequest request
    ) {
        return ResponseEntity.status(201).body(buildingService.create(clientId, request));
    }

    @GetMapping("/clients/{clientId}/buildings")
    public List<BuildingResponse> listByClient(@PathVariable Long clientId) {
        return buildingService.listByClient(clientId);
    }

    @GetMapping("/buildings/{buildingId}")
    public BuildingResponse getById(@PathVariable Long buildingId) {
        return buildingService.getById(buildingId);
    }

    @PutMapping("/buildings/{buildingId}")
    public BuildingResponse update(
            @PathVariable Long buildingId,
            @Valid @RequestBody BuildingUpdateRequest request
    ) {
        return buildingService.update(buildingId, request);
    }
}
