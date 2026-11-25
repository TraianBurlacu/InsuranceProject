package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.ReportAdminService;
import com.endava.insuranceapp.application.service.report.ReportFilter;
import com.endava.insuranceapp.web.dto.PolicyReportRow;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportsController {

    private final ReportAdminService service;

    public AdminReportsController(ReportAdminService service) {
        this.service = service;
    }

    @GetMapping("/policies-by-country")
    public ResponseEntity<List<PolicyReportRow>> byCountry(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String buildingType
    ) {
        var filter = new ReportFilter(from, to, status, currency, buildingType);
        return ResponseEntity.ok(service.reportByCountry(filter));
    }

    @GetMapping("/policies-by-county")
    public ResponseEntity<List<PolicyReportRow>> byCounty(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String buildingType
    ) {
        var filter = new ReportFilter(from, to, status, currency, buildingType);
        return ResponseEntity.ok(service.reportByCounty(filter));
    }

    @GetMapping("/policies-by-city")
    public ResponseEntity<List<PolicyReportRow>> byCity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String buildingType
    ) {
        var filter = new ReportFilter(from, to, status, currency, buildingType);
        return ResponseEntity.ok(service.reportByCity(filter));
    }

    @GetMapping("/policies-by-broker")
    public ResponseEntity<List<PolicyReportRow>> byBroker(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String buildingType
    ) {
        var filter = new ReportFilter(from, to, status, currency, buildingType);
        return ResponseEntity.ok(service.reportByBroker(filter));
    }
}
