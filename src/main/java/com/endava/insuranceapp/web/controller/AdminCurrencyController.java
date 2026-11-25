package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.CurrencyAdminService;
import com.endava.insuranceapp.web.dto.CurrencyCreateRequest;
import com.endava.insuranceapp.web.dto.CurrencyResponse;
import com.endava.insuranceapp.web.dto.CurrencyUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/currencies")
public class AdminCurrencyController {

    private final CurrencyAdminService currencyAdminService;

    public AdminCurrencyController(CurrencyAdminService currencyAdminService) {
        this.currencyAdminService = currencyAdminService;
    }

    @GetMapping
    public List<CurrencyResponse> list() {
        return currencyAdminService.listAll();
    }

    @PostMapping
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyCreateRequest request) {
        return ResponseEntity.status(201).body(currencyAdminService.create(request));
    }

    @PutMapping("/{id}")
    public CurrencyResponse update(@PathVariable Long id, @Valid @RequestBody CurrencyUpdateRequest request) {
        return currencyAdminService.update(id, request);
    }
}
