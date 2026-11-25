package com.endava.insuranceapp.web.controller;

import com.endava.insuranceapp.application.service.GeographyService;
import com.endava.insuranceapp.web.dto.CityResponse;
import com.endava.insuranceapp.web.dto.CountryResponse;
import com.endava.insuranceapp.web.dto.CountyResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brokers")
public class GeographyController {

    private final GeographyService geographyService;

    public GeographyController(GeographyService geographyService) {
        this.geographyService = geographyService;
    }

    @GetMapping("/countries")
    public List<CountryResponse> getCountries() {
        return geographyService.getCountries();
    }

    @GetMapping("/countries/{countryId}/counties")
    public List<CountyResponse> getCounties(@PathVariable Long countryId) {
        return geographyService.getCountiesByCountry(countryId);
    }

    @GetMapping("/counties/{countyId}/cities")
    public List<CityResponse> getCities(@PathVariable Long countyId) {
        return geographyService.getCitiesByCounty(countyId);
    }
}
